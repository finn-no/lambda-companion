package no.finn.lambdacompanion

import spock.lang.Specification

class ExtendedStreamSpec extends Specification {


    def "Find last returns last element"() {
        given:
        def stream = ExtendedStream.of(elements.stream())

        when:
        def last = stream.findLast()

        then:
        last == expected

        where:
        elements                                                | expected
        []                                                      | StreamableOptional.empty()
        ["hello world"]                                         | StreamableOptional.of("hello world")
        ["h", "e", "l", "l", "o", " ", "w", "o", "r", "l", "d"] | StreamableOptional.of("d")
    }

}
