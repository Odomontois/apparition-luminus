(defn mod-nth [v i f] (assoc v i (f (v i))))
(defn run [oper nvecs nitems nthreads niters]
  (let [vec-refs (vec (map (comp ref vec)
                        (partition nitems (repeat (* nvecs nitems) 0))))
        sum  #(reduce + %)
        swap #(let [v1 (rand-int nvecs)
                    v2 (rand-int nvecs)
                    i1 (rand-int nitems)
                    i2 (rand-int nitems)]
                (dosync
                  (let [temp (nth @(vec-refs v1) i1)]
                    (oper (vec-refs v1) mod-nth i1 inc)
                    (oper (vec-refs v2) mod-nth i2 dec))))
        report #(do
                  (prn (map deref vec-refs))
                  (println "Sum:"
                    (reduce + (map (comp sum deref) vec-refs))))]
    (report)
    (dorun (apply pcalls (repeat nthreads #(dotimes [_ niters] (swap)))))
    (report)))

(time (run alter 100 10 10 100000))
