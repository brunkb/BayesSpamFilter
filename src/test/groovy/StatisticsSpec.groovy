import spock.lang.Specification
import spock.lang.Unroll
import java.math.MathContext
import java.math.RoundingMode

/**
 * Created by UC192330 on 3/2/2016.
 */
class StatisticsSpec extends Specification {

    @Unroll
    def "test bayesian probability"() {

        when:
        def result = Statistics.bayesianSpamProbability(hamFreq, spamFreq, assumedProb, weight, totalHam, totalSpam)

        then:

        result == expected

        where:
            expected              | hamFreq | spamFreq | assumedProb | weight | totalHam | totalSpam
            0.5                   | 1       | 1        | 0.5         | 1.0    | 100      | 100
            0.03333333333333333   | 72      | 2        | 0.5         | 1.0    | 100      | 100
            0.0049566677365990965 | 200     | 1        | 0.5         | 1.0    | 200      | 100
            0.9101508916323731    | 1       | 25       | 0.5         | 1.0    | 200      | 100

    }

    @Unroll
    def "test spamProbability"() {

        when:
        def result = Statistics.spamProbability(hamFrequency, spamFrequency, totalHam, totalSpam)

        then:
        result == calculation

        where:
        calculation          | cljCalculation | hamFrequency  | spamFrequency | totalHam | totalSpam
        0.5                  | "1/2"          | 1             | 1             | 1        | 1
        0.5                  | "1/2"          | 1             | 1             | 0        | 0
        0.9090909090909091   | "10/11"        | 1             | 10            | 0        | 0
        0.09090909090909091  | "1/11"         | 10            | 1             | 0        | 0
        0.09090909090909091  | "1/11"         | 10            | 1             | 10       | 10
        0.009900990099009901 | "1/101"        | 10            | 1             | 10       | 1

    }

    @Unroll
    def "test fisher computation"() {

        when:
        def result = Statistics.parallelColtFisher(probabilities, degreesOfFreedom) as Double

        BigDecimal bdResult = new BigDecimal(result, MathContext.DECIMAL64)
        then:
        bdResult.round(new MathContext(4, RoundingMode.HALF_UP)) == expected.round(new MathContext(4, RoundingMode.HALF_UP))

        where:

            expected             |  probabilities       | degreesOfFreedom
            0.053050570013819076 | [0.1, 0.1, 0.2]      | 3
            0.35458235871706656  | [0.5, 0.2, 0.4, 0.9] | 3
            0.729                | [0.9, 0.9, 0.9]      | 1
            1.0                  | [1.0]                | 1
            0.10000000000000009  | [0.1]                | 1
    }
}
