import clojure.java.api.Clojure
import clojure.lang.IFn
import clojure.lang.Keyword
import clojure.lang.Ratio
import spock.lang.Specification

/**
 * Created by UC192330 on 3/2/2016.
 */
class StatisticsSpec extends Specification {

    def "test bayesian probability"() {

        when:
        def result = Statistics.bayesianSpamProbability([spamFrequency: 1, hamFrequency: 1],
                0.5, 1.0, 100, 100)

        def result2 = Statistics.bayesianSpamProbability([spamFrequency: 72, hamFrequency: 2],
                0.5, 1.0, 100, 100)
        println result2

        def result3 = Statistics.bayesianSpamProbability([spamFrequency: 200, hamFrequency: 1],
                0.5, 1.0, 200, 100)
        println result3

        def result4 = Statistics.bayesianSpamProbability([spamFrequency: 1, hamFrequency: 25],
                0.5, 1.0, 200, 100)
        println result4

        then:
        result == 0.5
    }

    def "test spamProbability"() {

        when:
        def result = Statistics.spamProbability(feature, totalHam, totalSpam)

        IFn require = Clojure.var("clojure.core", "require")
        require.invoke(Clojure.read("spam.bayes"))

        IFn cljSpamProb = Clojure.var("spam.bayes", "spam-probability-with-totals")
        Ratio cljResult = cljSpamProb.invoke(feature['spamFrequency'], feature['hamFrequency'], totalHam, totalSpam)

        cljResult.toString()
        println "${result}    ${cljResult}"

        then:
        result == calculation
        cljResult.toString() == cljCalculation

        where:
        calculation          | cljCalculation | feature                              | totalHam | totalSpam
        0.5                  | "1/2"          | [spamFrequency: 1, hamFrequency: 1]  | 1        | 1
        0.5                  | "1/2"          | [spamFrequency: 1, hamFrequency: 1]  | 0        | 0
        0.9090909090909091   | "10/11"        | [spamFrequency: 10, hamFrequency: 1] | 0        | 0
        0.09090909090909091  | "1/11"         | [spamFrequency: 1, hamFrequency: 10] | 0        | 0
        0.09090909090909091  | "1/11"         | [spamFrequency: 1, hamFrequency: 10] | 10       | 10
        0.009900990099009901 | "1/101"        | [spamFrequency: 1, hamFrequency: 10] | 10       | 1

    }

    // Demonstrates how to call a Clojure function from Groovy
    def "test calling Clojure functions"() {

        when:
        IFn plus = Clojure.var("clojure.core", "+")
        def result = plus.invoke(1, 2)
        then:
        result == 3
    }

    def "test fisher computation"() {

        when:
        def result = Statistics.fisher([0.1, 0.1, 0.2], 3)
        then:
        result == 0.053050570013819076
    }
}
