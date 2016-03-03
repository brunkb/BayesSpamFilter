import groovy.transform.ToString

@ToString
class Result {
    String fileName
    Classification actualType
    Classification calculatedType
    Double spamScore
}
