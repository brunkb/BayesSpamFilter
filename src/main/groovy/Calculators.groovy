class Calculators {

    final static def MIN_SPAM_SCORE = 0.9
    final static def MAX_HAM_SCORE = 0.4

    static def classifyScore = { def score ->
        if (score <= MAX_HAM_SCORE) {
            Classification.HAM
        } else if (score >= MIN_SPAM_SCORE) {
            Classification.SPAM
        } else {
            Classification.UNSURE
        }
    }

    static def evaluateResult = { Result result ->

                        // left is calculatedType, right is actualType
        Map lookup = [([Classification.HAM, Classification.HAM]): ResultType.CORRECT,
                      ([Classification.SPAM, Classification.SPAM]): ResultType.CORRECT,
                      ([Classification.HAM, Classification.SPAM]): ResultType.FALSE_NEGATIVE,
                      ([Classification.SPAM, Classification.HAM]): ResultType.FALSE_POSITIVE,
                      ([Classification.UNSURE, Classification.SPAM]): ResultType.MISSED_SPAM,
                      ([Classification.UNSURE, Classification.HAM]): ResultType.MISSED_HAM]

        lookup[[result.calculatedType, result.actualType]]
    }


   static def score = { Map features, long totalHam, long totalSpam ->

        List spamProbs = []
        features.each { key, val ->
            spamProbs << Statistics.bayesianSpamProbability(val['hamFrequency'], val['spamFrequency'], 0.5, 1.0, totalHam, totalSpam)
        }

        List hamProbs = spamProbs.collect { 1 - it }

        def h = 1 - Statistics.parallelColtFisher(spamProbs, features.size())
        def s = 1 - Statistics.parallelColtFisher(hamProbs, features.size())

        ((1 - h) + s) / 2
    }

    static def analyzeResults = { List results ->

        def finalStats = [Total: 0, (ResultType.CORRECT): 0,
                          (ResultType.FALSE_POSITIVE): 0, (ResultType.FALSE_NEGATIVE): 0,
                          (ResultType.MISSED_HAM): 0, (ResultType.MISSED_SPAM): 0]
        results.each {  Result r ->
                finalStats['Total'] += 1

                ResultType rt = Calculators.evaluateResult(r)

                if(rt == null) {
                    println "Something went wrong with: ${r.toString()}"
                } else {
                    finalStats[rt] += 1
                }
        }
        finalStats
    }

}
