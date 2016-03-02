(ns spam.bayes
    (:gen-class))

(def total-ham (agent 0))
(def total-spam (agent 0))

(defn spam-probability-with-totals [spam-count ham-count total-h total-s]
      (let [s (/ spam-count (max 1 total-h))
            h (/ ham-count (max 1 total-s))]
           (/ s (+ s h))))


(defn spam-probability [feature]
      (let [s (/ (:spam feature) (max 1 @total-ham))
            h (/ (:ham feature) (max 1 @total-spam))]
           (/ s (+ s h))))

(defn bayesian-spam-probability
      "Calculates probability a feature is spam on a prior. assumed-
      probability is the prior assumed for each feature, and weight is
      weight to be given to the prior (i.e. the number of data points to
      count it as).  Defaults to 1/2 and 1."
      [feature & {:keys [assumed-probability weight] :or
                        {assumed-probability 1/2 weight 1}}]
      (println "calculating probability")
      (let [basic-prob (spam-probability feature)
            total-count (+ (:spam feature) (:ham feature))]
           (/ (+ (* weight assumed-probability)
                 (* total-count basic-prob))
              (+ weight total-count))))