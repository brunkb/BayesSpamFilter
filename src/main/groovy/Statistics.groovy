import cern.jet.stat.tdouble.Probability

class Statistics {

    static def spamProbability = { long hamFrequency,
                                   long spamFrequency,
                                   long totalHam,
                                   long totalSpam ->

        double s = (double) spamFrequency / Math.max(1, totalHam)
        double h = (double) hamFrequency / Math.max(1, totalSpam)
        s / (s + h)
    }

    /**
     *  "Calculates probability a feature is spam on a prior.  AssumedProbability
     *  is the prior assumed for each feature, and weight is the
     *  weight to be given to the prior (i.e. the number of data points to
     *  count it as).  Defaults to 1/2 and 1."
     */
    static def bayesianSpamProbability = { long hamFrequency,
                                           long spamFrequency,
                                           double assumedProbability = 0.5,
                                           double weight = 1.0,
                                           long totalHam,
                                           long totalSpam ->
        def basicProb = Statistics.spamProbability(hamFrequency, spamFrequency, totalHam, totalSpam)
        def totalCount = hamFrequency + spamFrequency
        ((weight * assumedProbability) + (totalCount * basicProb)) / (weight + totalCount)

    }

    static def parallelColtFisher = { List probs, Long degreesOfFreedom ->
        Probability.chiSquareComplemented(degreesOfFreedom * 2, probs.collect { Math.log(it) }.sum() * -2)
    }
}
