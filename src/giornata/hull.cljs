(ns giornata.hull)

(defn point-sort
  [points]
  (sort (fn [[ax ay] [bx by]]
          (or (< ax bx)
              (and (= ax bx)
                   (< ay by))))
        points))

(defn signed-area
  [a b c]
  (* (/ 1 2)
     (+ (* (first b) (second a) -1)
        (* (first c) (second a))
        (* (first a) (second b))
        (* (first c) (second b) -1)
        (* (first a) (second c) -1)
        (* (first b) (second c)))))

(defn right-turn?
  [a b c]
  (< (signed-area a b c) 0))

(defn collinear?
  [a b c]
  (< (signed-area a b c) 0.0001))

;;[points (vec (concat (subvec hull 0 (- (count hull) 2)
;;                     (subvec hull (dec (count hull)) (count hull))))]

(defn step-hull
  [[points hull]]
  (cond (and (> (count hull) 2)
             (not (apply right-turn? (take-last 3 hull))))
        [points (concat (drop-last 2 hull)
                        [(last hull)])]

        (empty? points)
        [nil hull]
        
        :else
        [(rest points) (conj (vec hull) (first points))]))

(defn convex-hull
  [points]
  (let [sorted (point-sort points)
        upper (iterate step-hull [sorted []])
        lower (iterate step-hull [(reverse sorted) []])]
    [(take-while #(not (nil? (first %))) upper)
     (take-while #(not (nil? (first %))) lower)]))
