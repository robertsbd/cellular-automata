(ns cellular-automata.cellularcore
  (:gen-class))

;; this contains the code that is 

;; to generate the initial values of the matrix

(defn lazy-dead-alive-seq []
  "This will generate a row of random data for initial values"
  [] (repeatedly #(if (= 0 (rand-int 2)) :dead :alive)))

(defn init-matrix [w h]
  "this will create a matrix of height by width of random number"
  (take h (repeatedly #(take w (lazy-dead-alive-seq)))))

;; to move the rows north, south, east, west ready to allow computing the updated value

(defn move-rows-south [matrix w h]
  "moves all the rows south, adding a row of dead cells to the top row"
  (cons (repeat w :dead) (take (- h 1) matrix)))

(defn move-rows-north [matrix w]
  "moves all rows north, adding a row of dead cells to the bottom row"
  (seq
   (conj (vec (drop 1 matrix)) (repeat w :dead))))

(defn move-row-west [row]
  "moves a single row west, adding a dead cell to the end"
  (seq
   (conj (vec (drop 1 row)) :dead)))

(defn move-row-east [row w]
  "moves a single row east, adding a dead cell to the start"
  (cons :dead (take (- w 1) row)))

;; calculate the new-cell-value

(defn new-cell-value [current-cell-val surrounding-cells]
  "takes the values of the surrounding cells and returns the values of the new cells, values are 1 for alive 0 for dead"
  (let [alive-surrounding (count (filter #(= % :alive) surrounding-cells))]
    (cond
      (= current-cell-val :alive) (if (or (= alive-surrounding 2) (= alive-surrounding 3)) :alive :dead)
      (= current-cell-val :dead) (if (= alive-surrounding 3) :alive :dead))
    )
  )

;; calculate all the new values in a row

(defn new-row-value [current n s w]
  "runs new-cell-value across a row."
  (map
   #(new-cell-value %1 (list %2 %3 %4 %5 %6 %7 %8 %9))
   current
   n
   s
   (move-row-east current w)
   (move-row-west current)
   (move-row-east n w)
   (move-row-west n)
   (move-row-east s w)
   (move-row-west s)))

;; calculate the new values of the matrix

(defn new-matrix [matrix w h]
  "Update the matrix to the next epoch"
  (map
   #(new-row-value %1 %2 %3 w)
   matrix
   (move-rows-north matrix w)
   (move-rows-south matrix w h)))
