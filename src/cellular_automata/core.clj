(ns cellular-automata.core
  (:gen-class)
  (:use
         [cellular-automata.cellularcore :as cellularcore]
         [seesaw.core :as sc]
         [seesaw.graphics :as sg]
         [seesaw.color :as scol]))

;; define our queue and a push and pop operation, pushes onto the back, pops off the front

(def queue (ref clojure.lang.PersistentQueue/EMPTY)) ;; this is a queue that contains the states of the cellular-automata that we are to update to

(def refresh "refresh rate" 200)
(def cols "number of cols in the display" 60)
(def rows "number of rows in the display" 35)
(def cell-width "width of the rows" 20)
(def back-col "background colour" :darkgrey)
(def dead-col "colour of dead cells" :white)
(def alive-col "colour of alive cells" {1 :red 2 :orange 3 :yellow 4 :blue 5 :green 6 :violet})

(defn push-onto-rear-queue!
  "Push a value of the matrix onto the back of the queue"
  [item]
  (dosync (alter queue conj item)))

(defn pop-off-front-queue!
  "Pop a value of the front of the queue and return it"
  []
  (dosync
   (let [v (peek @queue)]
     (alter queue pop)
     v)))

(defn generate-cellular-automata
  "Generate cellular automata frames putting them onto the queue"
  [width height refresh]
  (do
    (loop [w width h height matrix (cellularcore/init-matrix width height)]
      (push-onto-rear-queue! matrix)
      (Thread/sleep refresh)
      (recur width height (cellularcore/new-matrix matrix width height))
      )
    )
  )

;; Graphical display

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
                 (sg/style :background (scol/color (if (= (second m) :alive) (alive-col (+ (int (* 6 (rand))) 1)) dead-col))))))))

(def main-canvas
  "The canvas"
  (sc/canvas :id :maincanvas
          :background back-col
          :paint paint-frame))

(def main-window
  "renders in a swing frame"
   (sc/frame :title "Cellular Automata"
             :content main-canvas))

(defn create-window!
  "Create a window of the given size"
  []
  (do
    (n)
    (sc/show! main-window)
    (sc/config! main-window :size [(* cols (inc cell-width)) :by (* (inc rows) (inc cell-width))]))) ;; this doesn't quite size the window correctly

(defn render-matrix-in-window!
  "To refresh the window with updated frame"
  []
    (sc/repaint! (sc/select main-window [:#maincanvas])))

(defn render-in-window
  "a process that will pop matrices from the queue and render them in a swing 
  window, want to render as fast as frames are being written"
  [refresh]
  (do
    (create-window!)
    (while 
        (loop []
          (when true
            (do
              (Thread/sleep refresh)
              (render-matrix-in-window!)))
          (recur)))))

(defn -main
  "main function"
  [& args]
  (do
    (future (generate-cellular-automata cols rows refresh))
    (future (render-in-window refresh))))
