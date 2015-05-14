(ns qttt.ui.om-ios
  (:require [qttt.ui.pre-om]
            [om.core :as om]
            [om.dom :as dont-use]
            [qttt.game :as game]))

;; Reset js/React back as the form above loads in an different React
(set! js/React (js/require "react-native/Libraries/react-native/react-native"))

(def css-transition-group (js/React.createFactory js/React.addons.CSSTransitionGroup))

(defn view
  [opts & children]
  (apply js/React.createElement js/React.View opts children))

(defn text
  [opts & children]
  (apply js/React.createElement js/React.Text opts children))

(defn table
  [opts & children]
  (apply js/React.createElement js/React.View opts children))

(defn tr
  [opts & children]
  (apply js/React.createElement js/React.View opts children))

(defn a
  [opts & children]
  (apply js/React.createElement js/React.View opts children))

(defn touchable-without-feedback
  [opts & children]
  (apply js/React.createElement js/React.TouchableWithoutFeedback opts children))

(defn class-name
  "Return a class name string given multiple CSS classes. Nil classes are filtered out."
  [& classes]
  (apply str (interpose " " (filter identity classes))))

(defn mark
  "Return a Om DOM node for a player's mark"
  [{:keys [player turn focus collapsing]}]
  (let [icon (if (= 0 player) "fa-plus" "fa-circle-o")
        player-class (if (= 0 player) "player-x" "player-o")]
    (view #js {:key       (str player)
               :className (class-name "mark" player-class (when focus "highlight")
                            (when collapsing "shake") (when collapsing "shake-constant"))}
      (view #js {:className (class-name "fa" icon)})
      (view #js {:className "turn"} turn))))

(defn entanglement
  "Om component for an individual entanglement"
  [e owner]
  (let [[_ cell _ subcell] (om/path e)
        game-cursor (om/root-cursor (om/state e))]
    (reify
      om/IRender
      (render [this]
        (touchable-without-feedback #js {:className    (class-name (if (empty? e) "empty-mark" "spooky-mark"))
                   :onPress      (fn [evt]
                                   (om/transact! game-cursor
                                     #(game/play (game/unspeculate %) cell subcell)))
                   :onPressIn (fn [evt]
                                   (om/transact! game-cursor
                                     #(game/speculate % cell subcell)))
                   :onPressOut (fn [evt]
                                   (om/transact! game-cursor game/unspeculate))}
          (text nil (if (empty? e) "e" "m"))
          #_(css-transition-group #js {:transitionName "mark-transition"}
            (when-not (empty? e) (mark e))))))))

(defn superposition
  "Om component for a quantum cell"
  [cell owner]
  (reify
    om/IRender
    (render [this]
      (view #js {:className (class-name "superposition")}
        (apply table #js {:style #js {:flexDirection   "row"
                                      :margin          40
                                      :backgroundColor "#EE1EEE"
                                      :justifyContent  "center"}}
          (map (fn [row]
                 (apply tr nil
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
      (view #js {:className "classical"}
        (mark (:classical cell))))))

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
      (let [[player phase] (game/instructions game)
            player-classes (if (zero? player)
                             ["player-x" "fa-plus"]
                             ["player-o" "fa-circle-o"])]
        (text #js {:className "instructions"}
          #_(view #js {:className (apply class-name "mark" "fa" player-classes)})
          (str "'s turn: " phase))))))

(defn board [game owner]
  (reify
    om/IRender
    (render [this]
      (view #js {:className "board-container"
                 :style     #js {:flexDirection   "row"
                                 :margin          40
                                 :backgroundColor "#EE1EEE"
                                 :justifyContent  "center"}}
        (om/build instructions game)
        (apply table #js {:className "board"
                          :style     #js {:flexDirection   "row"
                                          :margin          40
                                          :backgroundColor "#EE1EEE"
                                          :justifyContent  "center"}}
          (map (fn [row]
                 (apply tr {:style #js {:flexDirection   "row"
                                        :margin          40
                                        :backgroundColor "#EE1EEE"
                                        :justifyContent  "center"}}
                   (map (fn [idx]
                          (om/build cell
                            (get-in game [:board idx]))) row)))
            (partition 3 (range 9))))
        (view #js {:className "repo-link"}
          (text #js {:href "http://github.com/levand/qttt"}
            "http://github.com/levand/qttt"))))))

(defn screen [game owner]
  (reify
    om/IRender
    (render [this]
      (view #js {:className "play-area"
                 :style     #js {:flexDirection   "row"
                                 :margin          40
                                 :backgroundColor "#EE1EEE"
                                 :justifyContent  "center"}}
        (om/build board game)))))


(defn ^:export main
  []
  (let [game-state (atom game/new-game)]
    (om/root
      screen
      game-state
      {:target 1})))
