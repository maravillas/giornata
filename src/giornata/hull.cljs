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
  [{:keys [points hull all-dropped]}]
  (cond (and (> (count hull) 2)
             (not (apply right-turn? (take-last 3 hull))))
        (let [dropped (nth hull (- (count hull) 2))]
          {:op :drop
           :points points
           :hull (concat (drop-last 2 hull)
                         [(last hull)])
           :all-dropped (conj all-dropped dropped)
           :dropped dropped})

        (empty? points)
        {:op :fin
         :points nil
         :hull hull
         :all-dropped all-dropped}
        
        :else
        {:op :add
         :points (rest points)
         :hull (conj (vec hull) (first points))
         :all-dropped all-dropped}))

(defn convex-hull
  [points]
  (let [sorted (point-sort points)
        upper (iterate step-hull {:op :start
                                  :points sorted})
        lower (iterate step-hull {:op :start
                                  :points (reverse sorted)})]
    [(take-while #(not (= (:op %) :fin)) upper)
     (take-while #(not (= (:op %) :fin)) lower)]))
