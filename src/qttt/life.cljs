(ns qttt.life
  "Logic relating to life game state"
  (:require [clojure.set :as set]))

(comment
  ;; Game Data Structures

  ;; Game
  {:board board
   :timeout
   :running
   }

  ;; The board
  {0 cell 1 cell}

  ;; A cell
  {:alive true})

;; initial cell values, 1=alive & 0=dead
(def init-life [
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  [1 0 0 0 0 0 0 0 0 0 0 0]
  ])

;; select the integer at the specifed coordinate of the board
(defn cell-value
  [board row col]
  ((board row) col))

(def grid-size (count init-life))

(def app-state (atom {:board init-life
                      :timeout 1500
                      :running true}))

;; add numbers
(defn sum [vals] (reduce + vals))

;; board size mod-indices around an integer, inclusive of the integer
(defn surround
  [x]
  (map (fn [delta] (mod (+ x grid-size delta) grid-size)) [-1 0 1]))

;; the coordinates of the cell's neighbors
(defn get-neighbors
  [row col]
  (for [
    x (surround row)
    y (surround col)
    :when (not (= [row col] [x y]))]
    [x y]))

;; how many of the cells neighbors are alive
(defn count-alive-neighbors
  [board row col]
  (let [
    neighbors (get-neighbors row col)
    ]
    ;; loc is a (row col)
    (sum (map (fn [loc] (cell-value board (first loc) (last loc))) neighbors))))

;; is the cell alive at the current iteration?
(defn is-alive
  [board row col]
  (= ((board row) col) 1))

;; is the cell alive in the next iteration of the board?
(defn is-alive-next
  [board row col]
  (let [
    alive-neighbors (count-alive-neighbors board row col)
    ]
    (cond
      (< alive-neighbors 2) false
      (and (= alive-neighbors 2) (is-alive board row col)) true
      (= alive-neighbors 3) true ;; dead or alive
      (> alive-neighbors 3) false
      :else false)))

;; the next iteration of the board
(defn next-board
  [board]
  (let [
    grid-list (partition grid-size (for [
      row (range grid-size)
      col (range grid-size)
      ]
      (if (is-alive-next board row col) 1 0)))
    ]
    (vec (map vec grid-list))))
