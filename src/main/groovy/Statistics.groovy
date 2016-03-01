/**
 * Created by UC192330 on 3/1/2016.
 */
class Statistics {


    /**
     *  "Calculates probability a feature is spam on a prior.  AssumedProbability
     *  is the prior assumed for each feature, and weight is the
     *  weight to be given to the prior (i.e. the number of data points to
     *  count it as).  Defaults to 1/2 and 1."
     */
    static def bayesianSpamProbability = {


    }

    // Fisher computation described by Robinson
    static def fisher = {  List probs, Long num ->
        1 - chiSquare(probs.collect { Math.log(it) }.sum() * -2, num * 2)
    }

}
