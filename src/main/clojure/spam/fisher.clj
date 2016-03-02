(ns spam.fisher
(:require [incanter.stats :as stats])
    (:gen-class))

(defn fisher
      "Fisher computation described by Robinson."
      [probs num]
      (println "fisher computation")
      (- 1 (stats/cdf-chisq
             (* -2 (reduce + (map #(Math/log %1) probs)))
             :df (* 2 num))))

(defn -main [& args]
    (println "Starting up")
    (println (fisher (0.1 0.2 0.3) 3)))