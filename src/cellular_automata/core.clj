(ns cellular-automata.core
  (:gen-class)
  (:use
         [cellular-automata.cellularcore :as cellularcore]
         [seesaw.core :as sc]
         [seesaw.graphics :as sg]
         [seesaw.color :as scol]))

;; define our queue and a push and pop operation, pushes onto the back, pops off the front

(def queue (ref clojure.lang.PersistentQueue/EMPTY)) ;; this is a queue that contains the states of the cellular-automata that we are to update to

(def refresh 200)
(def cols 160)
(def rows 120)
(def cell-width 6)
(def dead-col :white)
(def alive-col :blue)

(defn push-onto-rear-queue!
  "Will push a value of the matrix onto the back of the queue"
  [item]
  (dosync (alter queue conj item)))

(defn pop-off-front-queue!
  "Will pop a value of the front of the queue and return it"
  []
  (dosync
   (let [v (peek @queue)]
     (alter queue pop)
     v)))

(defn generate-cellular-automata
  "a process that will generate cellular automata frames putting them onto the queue, control rate of animation by how fast we write
  to the queue."
  [width height refresh]
  (do
    (println "generating cellular automata")
    (loop [w width h height matrix (cellularcore/init-matrix width height)]
      (push-onto-rear-queue! matrix)
      (Thread/sleep refresh)
      (recur width height (cellularcore/new-matrix matrix width height))
      )
    )
  )

(defn render-matrix-in-console!
  "render a matrix frame in the console"
  [matrix]
  (doseq [l matrix]
    (println (map #(if (= % :dead) " " "*") l))))

(defn render-in-console 
  "a process that will pop matrices from the queue and render them in the console, will render as fast as frames are being written"
  []
  (while 
      (loop []
        (render-matrix-in-console! (pop-off-front-queue!))
        (recur))))

;; here is all the graphical information

(defn n []
  (sc/native!))

(defn paint-frame
  "define the operation for painting the frame with an animation from 
  from the queue"
  [c g]
  (let [matrix (pop-off-front-queue!)]
    (doseq [m-row (map-indexed vector matrix)]
      (doseq [m (map-indexed vector (second m-row))]
        (sg/draw g
                 (sg/rect (* (first m) (inc cell-width)) (* (first m-row) (inc cell-width)) cell-width cell-width)
                 (sg/style :background (scol/color (if (= (second m) :alive) alive-col dead-col))))))))

(def main-canvas
  (sc/canvas :id :maincanvas
          :background :white
          :paint paint-frame))

(def main-window
  "renders in a swing frame"
   (sc/frame :title "Cellular Automatat"
             :content main-canvas))

(defn create-window!
  []
  (do
    (n)
    (sc/show! main-window)
    (sc/config! main-window :size [(* cols (inc cell-width)) :by (* rows (inc cell-width))])))

(defn render-matrix-in-window!
  []
    (sc/repaint! (sc/select main-window [:#maincanvas])))

(defn render-in-window
  "a process that will pop matrices from the queue and render them in a swing 
  window, will render as fast as frames are being written"
  [refresh]
  (do
    (create-window!)
    (while 
        (loop []
          (render-matrix-in-window!)
          (Thread/sleep refresh)
          (recur)))))

(defn -main [& args]
  (do
    (future (generate-cellular-automata cols rows refresh))
    (future (render-in-window refresh))))
