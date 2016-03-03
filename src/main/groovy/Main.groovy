import groovy.io.FileType

class Main {

    static final String HAM_CORPUS = "src/main/resources/small_corpus/ham"
    static final String SPAM_CORPUS = "src/main/resources/small_corpus/spam"

    final static def CV_FRACTION = 1 / 5

    static long totalHam = 0
    static long totalSpam = 0
    static Map featureDb = [:] // a map of tokens to frequency of both spam and ham

    // Returns a map of the form [filename type]
    static Map populateCorpus() {
        def spamDir = new File(SPAM_CORPUS)
        def hamDir = new File(HAM_CORPUS)

        def corpus = [:]
        spamDir.eachFileRecurse(FileType.FILES) {
            corpus << [(it.path): (Classification.SPAM)]
        }

        hamDir.eachFileRecurse(FileType.FILES) {
            corpus << [(it.path): (Classification.HAM)]
        }

        corpus
    }

    static def updateFeatureDb = { tokenFeatures ->

        tokenFeatures.each { key, val ->

            if (!featureDb.containsKey(key)) {
                featureDb << [(key): val]
            } else {
                def entry = featureDb[key]
                entry['spamFrequency'] += val['spamFrequency']
                entry['hamFrequency'] += val['hamFrequency']
                featureDb[key] = entry
            }
        }
    }

    // The "training" consists of reading in a random set of the corpus data,
    // tokenizing it, then indicating whether it is ham or spam
    static def trainFromCorpus = { Map corpus, Long trainingSize ->
        List<String> keys = new ArrayList(corpus.keySet())

        Collections.shuffle(keys)

        keys.eachWithIndex { String key, int idx ->

            if (idx >= trainingSize) {
                return
            }

            Map tokens = Extractor.extractTokensFromFile(key, corpus[key])
            Main.updateFeatureDb(tokens)

            if (corpus[key] == Classification.SPAM) {
                Main.totalSpam += 1
            } else {
                Main.totalHam += 1
            }
        }
        keys
    }

    // Tokenizes a message and then searches featureDb for those tokens in order
    // to build up a list of known tokens to compare with
    static def extractFeatures = {  String text ->

        List tokens = Extractor.tokenize(text)
        def result = [:]
        tokens.each { String tok ->

            def entry = featureDb.find { it.key == tok }
            if(entry) {
                result << entry
            }
        }
        result
    }

    // Tests the remaining corpus not used in the training set
    static def testFromCorpus = { Map corpus, List remainingKeys ->

        List results = []

        remainingKeys.each { key ->
            def f = new File(key)
            Map features = Main.extractFeatures(f.text)

            println "scoring for key: ${key}"
            def score = Calculators.score(features, Main.totalHam, Main.totalSpam)


            Classification classification = Calculators.classifyScore(score)

            Result result = new Result(fileName: key,
                                       actualType: corpus[key],
                                       calculatedType: classification,
                                       spamScore: score  )
            results << result
        }

        results
    }

    static void main(String[] args) {

        println "Starting up"
        Map corpus = populateCorpus()

        println "Corpus populated, Total Size:  ${corpus.size()}"

        def trainingSize = Math.round(corpus.size() * (1 - CV_FRACTION))
        println "Training..."
        def shuffledKeys = trainFromCorpus(corpus, trainingSize)

        // test from corpus
        println "Feature DB Size: ${featureDb.size()}"

        def remainingKeys = shuffledKeys.drop(trainingSize.intValue())
        println "Testing from remaining corpus keys remaining: ${remainingKeys.size()}"

        List results = Main.testFromCorpus(corpus, remainingKeys)

        println "Analyzing results"

        Map report = Calculators.analyzeResults(results)

        report.each { key, val ->
            println "${key}:  ${val}"
        }


        println "Done!"
    }

}



