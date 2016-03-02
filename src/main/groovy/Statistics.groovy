import clojure.java.api.Clojure
import clojure.lang.IFn
import clojure.lang.Keyword

class Statistics {

    static def spamProbability = { Map feature, Long totalHam, Long totalSpam ->

        double s = (double) feature['spamFrequency'] / Math.max(1, totalHam)
        double h = (double) feature['hamFrequency'] / Math.max(1, totalSpam)
        s / (s + h)
    }

    /**
     *  "Calculates probability a feature is spam on a prior.  AssumedProbability
     *  is the prior assumed for each feature, and weight is the
     *  weight to be given to the prior (i.e. the number of data points to
     *  count it as).  Defaults to 1/2 and 1."
     */
    static def bayesianSpamProbability = { Map feature,
                                           double assumedProbability = 0.5,
                                           double weight = 1.0,
                                           long totalHam,
                                           long totalSpam ->
        def basicProb = Statistics.spamProbability(feature, totalHam, totalSpam)
        def totalCount = feature['spamFrequency'] + feature['hamFrequency']
        ((weight * assumedProbability) + (totalCount * basicProb)) / (weight + totalCount)

    }

    // Fisher computation described by Robinson, I am calling the Clojure incanter.stats
    // library for now, but will work on substituting in a Java-based library
    static def fisher = { List probs, Long num ->

        IFn require = Clojure.var("clojure.core", "require")
        require.invoke(Clojure.read("incanter.stats"))

        IFn chiS = Clojure.var("incanter.stats", "cdf-chisq")

        // second arg is degrees of freedom
        1 - chiS.invoke(probs.collect { Math.log(it) }.sum() * -2, Keyword.find("df"), num * 2)
    }

}
