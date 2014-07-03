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
  [{:keys [points hull]}]
  (cond (and (> (count hull) 2)
             (not (apply right-turn? (take-last 3 hull))))
        {:op :drop
         :points points
         :hull (concat (drop-last 2 hull)
                        [(last hull)])
         :dropped (nth hull (- (count hull) 2))}

        (empty? points)
        {:op :fin
         :points nil
         :hull hull}
        
        :else
        {:op :add
         :points (rest points)
         :hull (conj (vec hull) (first points))}))

(defn convex-hull
  [points]
  (let [sorted (point-sort points)
        upper (iterate step-hull {:points sorted :op :start})
        lower (iterate step-hull {:points (reverse sorted) :op :start})]
    [(take-while #(not (= (:op %) :fin)) upper)
     (take-while #(not (= (:op %) :fin)) lower)]))
