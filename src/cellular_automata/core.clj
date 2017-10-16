(ns cellular-automata.core
  (:gen-class)
  (:use
         [cellular-automata.cellularcore :as cellularcore]
         [cellular-automata.swing.plotter :as p]
        ))

;; in here we want the core of the processes that take place

;; define our queue and a push and pop operation, pushes onto the back, pops off the front

(def queue (ref clojure.lang.PersistentQueue/EMPTY)) ;; this is a queue that contains the states of the cellular-automata that we are to update to

(def console-render (atom false)) ;; atom that will define if we are rendering to the console
(def graphic-render (atom false)) ;; atom that will define if we are rendering graphically

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

;; process to generate a cellular automata
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

;; a process to render into the console
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

(defn -main [& args]
  " first arg is width, second arg is height"
  (do
    (println "Welcome to life")
    (println "Enter width and height and refresh (ms): ")
    (let [width (read-string (read-line)) height (read-string (read-line)) refresh (read-string (read-line))]
      (future (generate-cellular-automata width height refresh))
      (future (render-in-console))
   ;;   (future (p/draw-frame))
      )
    )
  )
