import groovy.io.FileType

class Main {

    final static def CV_FRACTION = 1 / 5

    static Long totalHam = 0
    static Long totalSpam = 0
    static Map featureDb = [:] // a map of tokens to frequency of both spam and ham

    // Returns a map of the form [filename type]
    static Map populateCorpus() {
        def spamDir = new File("src/main/resources/corpus/spam")
        def hamDir = new File("src/main/resources/corpus/ham")

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
                totalSpam += 1
            } else {
                totalHam += 1
            }
        }
        keys
    }

    // Tokenizes a message and then searches featureDb for those tokens in order
    // to build up a list of known tokens to compare with
    def extractFeatures = {  String text ->

        List tokens = Extractor.tokenize(text)
        def result = []
        tokens.each { String tok ->

            result << featureDb.find { it.key == tok }
        }
        result
    }


    Classification classify = { String text ->

        def features = extractFeatures(text)
        def score = Calculators.score(features)
        Calculators.classifyScore(score)

    }

    // Tests the remaining corpus not used in the training set
    def testFromCorpus = { Map corpus, List remainingKeys ->

        remainingKeys.each {
            def f = new File(corpus[key])
            String text = f.text
            List tokens = Extractor.tokenize(text)
            classify()

            Result result = new Result(fileName: corpus[key])

        }


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

        featureDb.keySet().asList().eachWithIndex { entry, idx ->

            if(idx > 50) { return }
            println "${entry} -> ${featureDb[entry]}"

        }

        println "Testing from remaining corpus"

        def remainingKeys = shuffledKeys.drop(trainingSize.intValue())

        //testFromCorpus(corpus, remainingKeys)


        println "Done!"
    }

}



