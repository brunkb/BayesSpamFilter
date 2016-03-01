import spock.lang.Specification

class ExtractorIntegrationSpec extends Specification {


    def "test extractTokensFromFile"() {


        when:
        def f = new File("src/main/resources/corpus/spam/00001.317e78fa8ee2f54cd4890fdc09ba8176")
        println f.text

        Map tf = Extractor.extractTokensFromFile("src/main/resources/corpus/spam/00001.317e78fa8ee2f54cd4890fdc09ba8176",
                                Classification.SPAM)

        println tf

        then:
         true == true

    }

    def "test extractTokens"() {
        when:

        String text = """From:test@test.com
                         Subject:spam!
                         This is a spam message, bucko!
                        """

        Map tf = Extractor.extractTokens(text, Classification.SPAM)

        then:
        tf.size() == 12
        tf['From']['spamFrequency'] == 1

    }


    def "test tokenize"() {

        when:
        String text = """From:test@test.com
                         Subject:spam!
                         This is a spam message, bucko!
                        """

        List tf = Extractor.tokenize(text)
        println tf
        then:
        tf != null
        tf.size() == 14

    }

}
