import clojure.java.api.Clojure
import clojure.lang.IFn
import clojure.lang.Ratio
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by UC192330 on 3/2/2016.
 */
class StatisticsSpec extends Specification {

    def "test bayesian probability"() {

        when:
        def result = Statistics.bayesianSpamProbability(1, 1, 0.5, 1.0, 100, 100)

        def result2 = Statistics.bayesianSpamProbability(72, 2, 0.5, 1.0, 100, 100)

        def result3 = Statistics.bayesianSpamProbability(200, 1, 0.5, 1.0, 200, 100)

        def result4 = Statistics.bayesianSpamProbability(1, 25, 0.5, 1.0, 200, 100)

        then:
        result == 0.5
        result2 == 0.03333333333333333
        result3 == 0.0049566677365990965
        result4 == 0.9101508916323731
    }

    @Unroll
    def "test spamProbability"() {

        when:
        def result = Statistics.spamProbability(hamFrequency, spamFrequency, totalHam, totalSpam)

        IFn require = Clojure.var("clojure.core", "require")
        require.invoke(Clojure.read("spam.bayes"))

        IFn cljSpamProb = Clojure.var("spam.bayes", "spam-probability-with-totals")
        Ratio cljResult = cljSpamProb.invoke(spamFrequency, hamFrequency, totalHam, totalSpam)

        cljResult.toString()
        //println "${result}    ${cljResult}"

        then:
        result == calculation
        cljResult.toString() == cljCalculation

        where:
        calculation          | cljCalculation | hamFrequency  | spamFrequency | totalHam | totalSpam
        0.5                  | "1/2"          | 1             | 1             | 1        | 1
        0.5                  | "1/2"          | 1             | 1             | 0        | 0
        0.9090909090909091   | "10/11"        | 1             | 10            | 0        | 0
        0.09090909090909091  | "1/11"         | 10            | 1             | 0        | 0
        0.09090909090909091  | "1/11"         | 10            | 1             | 10       | 10
        0.009900990099009901 | "1/101"        | 10            | 1             | 10       | 1

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
