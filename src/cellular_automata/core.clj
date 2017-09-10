(ns cellular-automata.core
  (:gen-class)
  (:require [clojure.core.matrix :as m])
  (:use [clojure.pprint]))

(def height 60)
(def width 60)

(defn init-matrix [w h]
  "this will create a matrix of height by width of random number"
  (vec (take h (repeatedly
                #(vec (take w (repeatedly
                              (fn [] (rand-int 2)))))))))

;; construct 4 lists that contain that required values of north, south, east, and west

(defn empty-top-row [mat]
  "change all values in the top row to empty"
  (vec
   (cons
   (vec (repeat width 0))
   (vec (take (- width 1) mat)))))

(defn empty-bot-row [mat]
  "change all values in the bottom row to empty"
  (conj
   (vec (drop 1 mat))
   (vec (repeat width 0))))

(defn get-north [mat]
  (empty-top-row mat))

(defn get-south [mat]
  (empty-bot-row mat))

(defn get-west [mat]
  (m/transpose (empty-top-row mat)))

(defn get-east [mat]
  (m/transpose (empty-bot-row mat)))

(defn move-row-east [row]
  (conj (vec (drop 1 row)) 0))

(defn move-row-west [row]
  (vec (cons 0 (take (- width 1) row))))

(defn new-cell-value [c n s e w ne nw se sw]
  "takes the values of the currounding cells and returns the values of the new cells, values are 1 for alive 0 for dead"
  (let [total (+ n s e w ne nw se sw)]
    (if (= c 1)
      (if (or (= total 2) (= total 3))
        1
        0)
      (if (= total 3)
        1
        0))))

(defn new-row-value [c n s e w]
  "runs new-cell-value across a row. We compute the north-east, north-west ... rows by shifting rows east or west"
  (vec
   (map
    #(new-cell-value %1 %2 %3 %4 %5 %6 %7 %8 %9)
    c n s e w (move-row-east n) (move-row-west n) (move-row-east s) (move-row-west s))))

(defn update-matrix [mat]
  "Update the matrix to the next epoch"
  (vec (map
   #(new-row-value %1 %2 %3 %4 %5)
   mat
   (get-north mat)
   (get-south mat)
   (get-east mat)
   (get-west mat))))

(defn render-output! [mat]
  (doseq [l mat]
    (println (map #(if (= % 0) " " "*") l))))
   

(defn -main
  []
  (loop [m (init-matrix width height)]
         (println)
         (render-output! m)
         (Thread/sleep 50)
         (recur (update-matrix m))))

