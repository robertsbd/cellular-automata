(ns cellular-automata.cellularcore
  (:gen-class)
  (:require [clojure.core.matrix :as m]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Code responsible for operations on the data model. ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn init-matrix
  "Create a matrix of cols by rows of random values of 0 or 1"
  [cols rows]
  (m/matrix
   (repeatedly rows #(map rand-int (repeat cols 2)))))

(defn new-cell-function
  "takes the values of the surrounding cells and returns the values of the new cells, values are 1 for alive 0 for dead"
  [cell-val sum-surrounding]
  (if (or (= sum-surrounding 3) (and (= sum-surrounding 2) (= cell-val 1))) 1 0))  

(defn updated-matrix
  "Calculate the new values of the matrix"
  [m]
  (let
      [d (m/shift m 0 -1)
       u (m/shift m 0 1)
       l (m/shift m 1 1)
       r (m/shift m 1 -1)
       dl (m/shift d 1 1)
       dr (m/shift d 1 -1)
       ul (m/shift u 1 1)
       ur (m/shift u 1 -1)]
    (m/emap #(new-cell-function %1 %2) m (m/add d u l r dl dr ul ur))))
