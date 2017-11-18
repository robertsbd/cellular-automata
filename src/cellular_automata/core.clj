(ns cellular-automata.core
  (:gen-class)
  (:use
         [cellular-automata.cellularcore :as cellularcore]
         [seesaw.core :as sc]
         [seesaw.graphics :as sg]
         [seesaw.color :as scol])
  (:import
         [java.awt Toolkit]))

;; define our queue and a push and pop operation, pushes onto the back, pops off the front

(def queue (ref clojure.lang.PersistentQueue/EMPTY)) ;; this is a queue that contains the states of the cellular-automata that we are to update to

(def back-col "background colour" :yellow)
(def dead-col "colour of dead cells" :yellow)
(def alive-col "colour of alive cells" :blue)

(def refresh "refresh rate" 100)
(def screen-size "resolution of the screen" (.getScreenSize (Toolkit/getDefaultToolkit)))
(def cell-size "length and width of cells" 20)

(def cols "number of cols in the display" (/ (.width screen-size) cell-size))
(def rows "number of rows in the display" (/ (.height screen-size) cell-size))

;; queue functions

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

;; Graphical display

(defn paint-frame
  "define the operation for painting the frame with an animation from 
  from the queue"
  [c g]
  (let [matrix (pop-off-front-queue!)]
    (doseq [m-row (map-indexed vector matrix)]
      (doseq [m (map-indexed vector (second m-row))]
        (sg/draw g
                 (sg/rect (* (first m) (inc cell-size)) (* (first m-row) (inc cell-size)) cell-size cell-size)
                 (sg/style :background (scol/color (if (= (second m) :alive) alive-col dead-col))))))))

(def main-canvas
  "The canvas"
  (sc/canvas :id :maincanvas
             :background back-col
             :paint paint-frame))

(def main-window
  "create the window to render the frames in"
  (sc/frame :title "Cellular Automata"
            :content main-canvas))

(defn create-window!
  "Create a window of the given size"
  []
  (-> main-window
      (full-screen!)
      (sc/show!)))

(defn generate-cellular-automata
  "Generate cellular automata frames putting them onto the queue and paint them"
  [width height refresh]
  (do
    (create-window!)
    (push-onto-rear-queue! (cellularcore/init-matrix width height)) ;; have one frame in the buffer so that we don't display empty data
    (loop [w width h height matrix (cellularcore/new-matrix (peek @queue) width height)]
      (push-onto-rear-queue! matrix)
      (Thread/sleep refresh)
      (sc/repaint! (sc/select main-window [:#maincanvas]))
      (recur width height (cellularcore/new-matrix matrix width height)))))

(defn -main
  "main function"
  [& args]
  (future (generate-cellular-automata cols rows refresh)))
