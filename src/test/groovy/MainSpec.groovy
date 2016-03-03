import spock.lang.Specification

/**
 * Created by UC192330 on 3/3/2016.
 */
class MainSpec extends Specification {


    def "test extractFeatures"() {

        when:
            Main.featureDb = ['the': [spamFrequency: 0, hamFrequency: 1],
                              'lazy': [spamFrequency: 1, hamFrequency: 7],
                              'dog': [spamFrequency: 1, hamFrequency: 5],
                              'jumped': [spamFrequency: 1, hamFrequency: 12]]

            def result = Main.extractFeatures("the lazy dog jumped in a car")

            result.each { key, val ->
                println val['spamFrequency']
            }

        then:
            result.size() == 4
            result.containsKey('in') == false
            result.containsKey('a') == false
            result.containsKey('car') == false

    }


}
