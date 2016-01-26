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

    def "Flat map optional returns present only"() {
        given:
        def stream = ExtendedStream.of((0..10).stream())

        when:
        def evens = stream.flatMapOptional({ i -> i % 2 == 0 ? Optional.of(i) : Optional.empty() }).toList()

        then:
        evens == [0, 2, 4, 6, 8, 10]
    }

    def "Flat map collection returns all elements in order"() {
        given:
        def stream = ExtendedStream.of(["Hello", " ", "World", "!"].stream())

        when:
        def chars = stream.flatMapCollection({ String word -> (word.toCharArray() as List) })

        then:
        "Hello World!".getChars() as List == chars.toList()
    }

}
