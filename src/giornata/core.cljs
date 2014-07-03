(ns giornata.core
  (:require [enchilada :refer [canvas ctx value-of canvas-size]]
            [jayq.core :refer [show]]
            [monet.core :refer [animation-frame]]
            [monet.canvas :refer [save restore
                                  circle
                                  begin-path move-to line-to close-path 
                                  stroke stroke-style fill fill-rect fill-style 
                                  rotate translate]]
            [giornata.hull :refer [convex-hull]]
            [giornata.gen :refer [gen-points]]))

(enable-console-print!)

(def width (first (canvas-size)))
(def height (second (canvas-size)))
(def padding 50)

(defn points
  [w h]
  (gen-points 20
              padding (- width padding)
              padding (- height padding)))

(defn draw-points
  [ctx points]
  (doseq [[x y] points]
    (circle ctx {:x x :y y :r 3})
    (stroke ctx))
  ctx)

(defn render!
  [ctx {:keys [points op hull dropped all-dropped]}]
  (-> ctx
      (fill-style :white)
      (fill-rect {:x 0 :y 0 :w width :h height})

      (fill-style :white)
      (stroke-style :#6A4A3C)
      (draw-points points)

      (fill-style :#00A0B0)
      (stroke-style :transparent)
      (draw-points hull)
      
      (fill-style :#EB6841)
      (stroke-style :transparent)
      (draw-points all-dropped)
      
      (fill-style :#00CC33)
      (stroke-style :transparent)
      (draw-points [dropped])))

(defn animate [ctx steps]
  (doseq [step (first steps)]
    (animation-frame identity)
    (render! ctx step)))

(show canvas)
;;(animate ctx (convex-hull (points width height)))
(animate ctx (convex-hull [[20 120] [30 70] [60 180] [120 60] [160 140]]))
