import spock.lang.Specification

/**
 * Created by UC192330 on 2/26/2016.
 */
class CalculatorsSpec extends Specification {

    def "test evaluateResult"() {

        when:
        Result r = new Result(actualType: actualType, calculatedType: calculatedType)
        ResultType rt = Calculators.evaluateResult(r)

        then:
        rt == expected

        where:

        expected                  |   actualType             |  calculatedType
        ResultType.CORRECT        |   Classification.SPAM    |  Classification.SPAM
        ResultType.CORRECT        |   Classification.HAM     |  Classification.HAM
        ResultType.MISSED_SPAM    |   Classification.UNSURE  |  Classification.SPAM
        ResultType.MISSED_HAM     |   Classification.UNSURE  |  Classification.HAM
        ResultType.FALSE_POSITIVE |   Classification.SPAM    |  Classification.HAM
        ResultType.FALSE_NEGATIVE |   Classification.HAM     |  Classification.SPAM

    }


    def "test spamProbability"() {

        when:
        def result = Calculators.spamProbability(feature, totalHam, totalSpam)

        then:
          result == calculation

        where:

            calculation  |  feature                              | totalHam | totalSpam
              2.0         |  [spamFrequency: 1, hamFrequency: 1]  | 1        | 1
              2.0         |  [spamFrequency: 1, hamFrequency: 1]  | 0        | 0
             11.0         |  [spamFrequency: 10, hamFrequency: 1] | 0        | 0
             10.1         |  [spamFrequency: 1, hamFrequency: 10] | 0        | 0
              1.1         |  [spamFrequency: 1, hamFrequency: 10] | 10       | 10
              2.0         |  [spamFrequency: 1, hamFrequency: 10] | 10       | 1

    }


}
