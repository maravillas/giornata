(ns giornata.gen)

(defn gen-points
  "Generates a vector of n points with coordinates in [0, max-x) and [0, max-y)."
  [n min-x max-x min-y max-y]
  (for [i (range n)]
    [(+ (rand-int (- max-x min-x)) min-x)
     (+ (rand-int (- max-y min-y)) min-y)]))
