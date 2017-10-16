(ns cellular-automata.swing.plotter
  (:gen-class)
  (:use seesaw.core
        seesaw.graphics
        seesaw.color))

(defn n []
  (native!))

(defn draw-a-red-x
  "Draw a red X on a widget with the given graphics context"
  [c g]
  (let [w          (width c)
        h          (height c)
        line-style (style :foreground "#FF0000" :stroke 3 :cap :round)
        d 5]
    (draw g
      (line d d (- w d) (- h d)) line-style
      (line (- w d) d d (- h d)) line-style)))

(defn main-canvas []
  (canvas :id :maincanvas
          :background :white))


(defn content []
  (vertical-panel :items (main-canvas)))


(defn draw-frame
  "renders in a swing frame"
  []
  (->
   (frame :title "Example"
          :content (content))
   pack!
   show!))

