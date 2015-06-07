(ns qttt.ui.om-ios
  (:require [qttt.ui.pre-om]
            [om.core :as om]
            [om.dom :as dont-use]
            [qttt.life :as life]))

;; Reset js/React back as the form above loads in an different React
(set! js/React (js/require "react-native/Libraries/react-native/react-native"))

(defn view
  [opts & children]
  (apply js/React.createElement js/React.View opts children))

(defn text
  [opts & children]
  (apply js/React.createElement js/React.Text opts children))

(defn touchable-without-feedback
  [opts & children]
  (apply js/React.createElement js/React.TouchableWithoutFeedback opts children))

(defn touchable-highlight
  [opts & children]
  (apply js/React.createElement js/React.TouchableHighlight opts children))

(defn slider
  [opts & children]
  (apply js/React.createElement js/React.SliderIOS opts children))

(defn toggle
  [opts & children]
  (apply js/React.createElement js/React.SwitchIOS opts children))

;; ascii character to mark a cell
(defn mark-text
  [cell-value]
  (if (= cell-value 1)
    "\u2B55"   ;; circle
    "\u2795")) ;; plus

;; color to mark cell
;; alive = red
;; dead = black
(defn mark-color
  [cell-value]
  (if (= cell-value 1) "#FF6629" "#D8D8D8"))

(defn cell
  "Om component for a cell"
  [cell owner]
  (reify
    om/IRender
    (render [this]
      (touchable-highlight #js {:style #js {}
                                :underlayColor "red"
                                :onPress (fn []
                                  (println "pressed")
                                  (println (:value cell))
                                  (swap! life/app-state #(update-in % [:board (:row cell) (:col cell)] (fn [x]
                                    (if (= (:value cell) 1) 0 1))))
                                  )}
        (view #js {:style #js {:flexDirection  "row"
                               :borderColor    "#2675B1"
                               :borderWidth    1
                               :justifyContent "center"
                               :width          23
                               :height         23
                               :padding        3}}
          (text #js {:style #js {:textAlign "center" :color (mark-color (:value cell)) :fontSize 8}}
            (mark-text (:value cell))))))))

(defn board [life owner]
  (reify
    om/IRender
    (render [this]
      (view nil
        (apply view #js {:style #js {:flexDirection "row"
                                     :borderColor   "#2675B1"
                                     :borderWidth   1}}
          (map (fn [row]
                 (apply view {:style #js {:flexDirection "row"}}
                   (map (fn [col]
                          (om/build cell {:value (get-in life [:board row col])
                                          :row row
                                          :col col})) (range life/grid-size))))
            (range life/grid-size)))
        (text #js {:textAlign "center"}
          "http://github.com/rothfels/evolution")
        (slider #js {:style #js {:height 10
                                 :margin 10}
                     :minimumValue 200
                     :maximumValue 2000
                     :value (:timeout life)
                     :onSlidingComplete (fn [slider-val] (swap! life/app-state #(update-in % [:timeout] (fn [x] slider-val))))})
        (toggle #js {:style #js {:marginBottom 10}
                     :value (:running life)
                     :onValueChange (fn [toggle-val]
                        (swap! life/app-state #(update-in % [:running] (fn [x] toggle-val))))})))))

(defn update-board
  [board]
  (life/next-board board))

(def interval-atom (atom nil))
(defn update-interval
  [life]
  (swap! interval-atom (fn [interval]
    (let [
      clear #(if interval (js/clearInterval interval))
      clear-and-restart (fn []
        (clear)
        (println "in clear and restart")
        (js/setInterval (fn [] (om/transact! life :board update-board)) (:timeout life)))
      ]
      (if (:running life) (clear-and-restart) (clear))))))

(defn screen [life owner]
  (let [isAlive (atom false)]
    (reify
      om/IRender
      (render [this]
        (let [
          timeout (update-interval life)
          ]
          (view #js {:style #js {:flexDirection   "row"
                                 :margin          20
                                 :backgroundColor "white"
                                 :justifyContent  "center"}}
            (om/build board life)))))))


(defn ^:export main
  []
  (om/root
    screen
    life/app-state
    {:target 1}))
