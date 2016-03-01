class Extractor {
    static def headerMatchers = ["To", "From", "Subject", "Return-Path"]
    static def tokenRegex = /[\w\-\$!]{3,}/


    static def extractTokensFromFile = { String fileName, Classification type ->
        def f = new File(fileName)
        String text = f.text
        Extractor.extractTokens(text, type)
    }


    static def extractTokens = { String text, Classification type ->

       def tokens = Extractor.tokenize(text)

        def spamFrequency = 0
        def hamFrequency = 0
        if(type == Classification.SPAM) {
            spamFrequency += 1
        } else {
            hamFrequency += 1
        }

        tokens.collectEntries { [(it): [spamFrequency: spamFrequency, hamFrequency: hamFrequency]]}
    }

    static def tokenize = { String text ->
        def tokens = text.findAll(tokenRegex)

        headerMatchers.each { def field ->
            def headerTokens = text.findAll("${field}:(.*)\n")

            headerTokens.flatten().each {
                def allHeaderTokens = it.findAll(tokenRegex)

                allHeaderTokens.each {
                    if (!headerMatchers.contains(it)) {
                        tokens << "${field}:${it}".toString()
                    }
                }
            }
        }
        tokens
    }
}
