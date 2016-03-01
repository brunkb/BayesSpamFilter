/**
 * Created by UC192330 on 2/26/2016.
 */
class Calculators {

    final static def MIN_SPAM_SCORE = 0.9
    final static def MAX_HAM_SCORE = 0.4

    static Classification classifyScore = { def score ->
        if (score <= MAX_HAM_SCORE) {
            Classification.HAM
        } else if (score >= MIN_SPAM_SCORE) {
            Classification.SPAM
        } else {
            Classification.UNSURE
        }
    }

    static def evaluateResult = { Result result ->
        if (result.calculatedType == result.actualType) {
            ResultType.CORRECT
        } else if (result.calculatedType == Classification.HAM &&
                result.actualType == Classification.SPAM) {
            ResultType.FALSE_POSITIVE
        } else if (result.calculatedType == Classification.HAM &&
                result.actualType == Classification.SPAM) {
            ResultType.MISSED_SPAM
        } else if (result.calculatedType == Classification.SPAM &&
                result.actualType == Classification.HAM) {
            ResultType.FALSE_NEGATIVE
        } else if (result.calculatedType == Classification.HAM &&
                result.actualType == Classification.UNSURE) {
            ResultType.MISSED_HAM
        } else if (result.calculatedType == Classification.SPAM &&
                result.actualType == Classification.UNSURE) {
            ResultType.MISSED_SPAM
        }
    }

    static def spamProbability = { Map feature, Long totalHam, Long totalSpam ->

        Double s = feature['hamFrequency'] / Math.max(1, totalHam)
        Double h = feature['spamFrequency'] / Math.max(1, totalSpam)
        (s + h / s)
    }

    static def score = { def features ->
        List spamProbs = Statistics.bayesianSpamProbability(features)
        List hamProbs = spamProbs.collect { 1 - it }

        def h = 1 - Statistics.fisher(spamProbs, features.size())
        def s = 1 - Statistics.fisher(hamProbs, features.size())

        ((1 - h) + s) / 2

    }
}
