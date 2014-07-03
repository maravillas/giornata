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
            [giornata.gen :refer [gen-points]]
            [cljs.core.async :as a :refer [chan <! >! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go alt!]]))

(enable-console-print!)

(def width (first (canvas-size)))
(def height (second (canvas-size)))
(def padding 50)
(def delay 250)

(defn points
  [w h]
  (gen-points 20
              padding (- width padding)
              padding (- height padding)))

(defn draw-points
  [ctx points]
  (doseq [[x y] points]
    (when (and x y)
      (circle ctx {:x x :y y :r 4})
      (stroke ctx)))
  ctx)

(defn draw-hull
  [ctx points]
  (when points
    (begin-path ctx)
    (doseq [[[ax ay] [bx by]] (partition 2 1 points)]
      (move-to ctx ax ay)
      (line-to ctx bx by))
    (close-path ctx))

  ctx)

(defn render!
  [ctx {:keys [points op hull dropped all-dropped]} & [other-hull]]
  (-> ctx
      (fill-style :white)
      (fill-rect {:x 0 :y 0 :w width :h height})

      (fill-style :white)
      (stroke-style :#6A4A3C)
      (draw-points points)

      (fill-style :#00A0B0)
      (stroke-style :transparent)
      (draw-points hull)

      (fill-style :#F9BF76)
      (stroke-style :transparent)
      (draw-points all-dropped)

      (fill-style :#EB6841)
      (stroke-style :transparent)
      (draw-points [dropped])

      (stroke-style :#00A0B0)
      (draw-hull hull)
      (stroke)
      (draw-hull other-hull)
      (stroke)))

(defn render-final!
  [ctx points upper lower]
  (-> ctx
      (fill-style :white)
      (fill-rect {:x 0 :y 0 :w width :h height})

      (fill-style :#F9BF76)
      (stroke-style :transparent)
      (draw-points points)

      (fill-style :#00A0B0)
      (stroke-style :transparent)
      (draw-points upper)
      (draw-points lower)

      (stroke-style :#00A0B0)
      (draw-hull upper)
      (stroke)
      (draw-hull lower)
      (stroke)))

(defn animate [ctx steps]
  (go
   (doseq [step (:upper steps)]
     (animation-frame identity)
     (render! ctx step)
     (<! (timeout delay)))
   (let [other-hull (:hull (last (:upper steps)))]
     (doseq [step (:lower steps)]
       (animation-frame identity)
       (render! ctx step other-hull)
       (<! (timeout delay))))

   (render-final! ctx
                  (:points steps)
                  (-> steps :upper last :hull)
                  (-> steps :lower last :hull))))

(show canvas)
(animate ctx (convex-hull (points width height)))
;;(animate ctx (convex-hull [[20 120] [30 70] [60 180] [120 60] [160 140]]))
