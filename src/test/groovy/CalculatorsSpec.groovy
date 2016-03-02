import spock.lang.Specification

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
}
