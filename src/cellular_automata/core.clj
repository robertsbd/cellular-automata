(ns cellular-automata.core
  (:gen-class)
  (:require
         [cellular-automata.datamodel :as dm]
         [seesaw.core :as sc]
         [seesaw.graphics :as sg]
         [seesaw.color :as scol]
         [clojure.core.matrix :as m])
  (:import
         [java.awt Toolkit]))

;; define our queue and a push and pop operation, pushes onto the back, pops off the front
(def queue (ref clojure.lang.PersistentQueue/EMPTY))

(def back-col "background colour" :yellow)
(def dead-col "colour of dead cells" :yellow)
(def alive-col "colour of alive cells" :blue)

(def refresh "refresh rate" 100)
(def screen-size "resolution of the screen" (.getScreenSize (Toolkit/getDefaultToolkit)))
(def cell-size "length and width of cells" 5)

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
  "Define the operation for painting the frame with data from the queue"
  [c g]
  (let [matrix (pop-off-front-queue!)
        matrix-as-seq (map vector (m/index-seq matrix) (m/eseq matrix))
        alive-vals (filter (fn [[[r c] v]] (= v 1.0)) matrix-as-seq)]
    (do
      ;; draw the background
      (sg/draw g
               (sg/rect 0 0 (.width screen-size) (.height screen-size))
               (sg/style :background (scol/color dead-col)))
      ;; draw the alive cells
      (doseq [[[r c] v] alive-vals ]
            (sg/draw g
                   (sg/rect (* c (inc cell-size)) (* r (inc cell-size)) cell-size cell-size)
                   (sg/style :background (scol/color alive-col)))))))

(def main-canvas
  "The canvas"
  (sc/canvas :id :maincanvas
             :background back-col
             :paint paint-frame))

(def main-window
  "Main window to render the frames in"
  (sc/frame :title "Cellular Automata"
            :content main-canvas))

(defn create-window!
  "Create a full screen window"
  []
  (-> main-window
      (sc/full-screen!)
      (sc/show!)))

(defn generate-cellular-automata
  "Generate cellular automata frames putting them onto the queue and paint them"
  [width height refresh]
  (do
    (create-window!)
    (push-onto-rear-queue! (dm/init-matrix cols rows))
    (loop [m (dm/update-matrix (peek @queue))]
      (push-onto-rear-queue! m)
      (Thread/sleep refresh)
      (sc/repaint! (sc/select main-window [:#maincanvas]))
      (recur (dm/update-matrix m)))))

(defn -main
  "main function"
  [& args]
  (future (generate-cellular-automata cols rows refresh)))
