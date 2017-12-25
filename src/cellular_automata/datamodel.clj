(ns cellular-automata.datamodel
  (:gen-class)
  (:require [clojure.core.matrix :as m]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Code responsible for operations on the data model. ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn init-matrix
  "Create a matrix of cols by rows of random values of 0 or 1"
  [cols rows]
  (m/matrix :vectorz
   (repeatedly rows #(map rand-int (repeat cols 2)))))

(defn update-matrix
  [m]
  (m/emap-indexed
   (fn [[r c] v]
     (let
         [c-start (max (- c 1) 0)
          c-length (if (or (= (+ c 1) (m/column-count m)) (= c 0)) 2 3)
          r-start (max (- r 1) 0)
          r-length (if (or (= (+ r 1) (m/row-count m)) (= r 0)) 2 3)
          surrounding  (- (m/esum (m/submatrix m [[r-start r-length] [c-start c-length]])) v)
          new-val (if
                      (or
                       (= surrounding 3.0)
                       (and
                        (= surrounding 2.0)
                        (= v 1.0)))
                    1.0 0.0)]
       new-val)) m))
