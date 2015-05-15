(ns qttt.ui.om-ios
  (:require [qttt.ui.pre-om]
            [om.core :as om]
            [om.dom :as dont-use]
            [qttt.game :as game]))

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

(defn mark-text
  [{:keys [player]}]
  (if (= 0 player)
    "\u2795"
    "\u2B55"))

(defn mark-color
  [e]
  (if (zero? (:player e))
    "#FFC929"
    "#FF6629"))

(defn entanglement
  "Om component for an individual entanglement"
  [e owner]
  (let [[_ cell _ subcell] (om/path e)
        game-cursor (om/root-cursor (om/state e))]
    (reify
      om/IRender
      (render [this]
        (touchable-without-feedback
          #js {:onPress    (fn [evt]
                             (om/transact! game-cursor
                               #(game/play (game/unspeculate %) cell subcell)))
               :onPressIn  (fn [evt]
                             (om/transact! game-cursor
                               #(game/speculate % cell subcell)))
               :onPressOut (fn [evt]
                             (om/transact! game-cursor game/unspeculate))}
          (view #js {:style #js {:flexDirection   "row"
                                 :width           30
                                 :height          30
                                 :justifyContent  "center"
                                 :borderRadius    3
                                 :backgroundColor (cond
                                                    (:focus e) "#333333"
                                                    (:collapsing e) "#224433"
                                                    :else "black")}}
            (text #js {:style #js {:color (mark-color e) :fontSize 18 :fontWeight (if (:focus e) "bold" "normal")}}
              (if (empty? e) "" (mark-text e)))
            (text #js {:style #js {:color "grey" :fontSize 6}}
              (:turn e))))))))

(defn superposition
  "Om component for a quantum cell"
  [cell owner]
  (reify
    om/IRender
    (render [this]
      (view nil
        (apply view #js {:style #js {:flexDirection  "row"
                                     :borderColor    "#2675B1"
                                     :borderWidth    1
                                     :justifyContent "center"}}
          (map (fn [row]
                 (apply view nil
                   (map (fn [idx]
                          ;; Make sure were have a valid cursor
                          (let [e (get-in cell [:entanglements idx]
                                    (get-in (assoc-in cell [:entanglements idx] {})
                                      [:entanglements idx]))]
                            (om/build entanglement e)))
                     row)))
            (partition 3 (range 9))))))))

(defn classical
  "Om component for a classical cell"
  [cell owner]
  (reify
    om/IRender
    (render [this]
      (view #js {:style #js {:flexDirection  "row"
                             :borderColor    "#2675B1"
                             :borderWidth    1
                             :justifyContent "center"
                             :width          92
                             :height         92
                             :padding        5}}
        (text #js {:style #js {:textAlign "center" :color (mark-color (:classical cell)) :fontSize 60}}
          (mark-text (:classical cell)))
        (text #js {:style #js {:fontSize 8 :color "grey"}}
          (:turn (:classical cell)))))))

(defn cell
  "Om component for a square"
  [cell owner]
  (if (:classical cell)
    (classical cell owner)
    (superposition cell owner)))

(defn instructions [game owner]
  (reify
    om/IRender
    (render [this]
      (let [[player phase] (game/instructions game)]
        (view #js {:style #js {:flexDirection  "row"
                               :justifyContent "center"}}
          (text #js {:style #js {:flexDirection "row"
                                 :margin        20
                                 :color         "#6DA6D2"
                                 :textAlign     "center"}}
            (str (if (zero? player) "\u2795" "\u2B55") "'s turn: " phase)))))))

(defn board [game owner]
  (reify
    om/IRender
    (render [this]
      (view nil
        (om/build instructions game)
        (apply view #js {:style #js {:flexDirection "row"
                                     :borderColor   "#2675B1"
                                     :borderWidth   1}}
          (map (fn [row]
                 (apply view {:style #js {:flexDirection "row"}}
                   (map (fn [idx]
                          (om/build cell
                            (get-in game [:board idx]))) row)))
            (partition 3 (range 9))))
        (text #js {:textAlign "center"}
          "http://github.com/levand/qttt")))))

(defn screen [game owner]
  (reify
    om/IRender
    (render [this]
      (view #js {:style #js {:flexDirection   "row"
                             :margin          20
                             :backgroundColor "black"
                             :justifyContent  "center"}}
        (om/build board game)))))


(defn ^:export main
  []
  (let [game-state (atom game/new-game)]
    (om/root
      screen
      game-state
      {:target 1})))