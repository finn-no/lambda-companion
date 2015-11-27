package no.finntech.lambdacompanion
import java.util.function.BiFunction

import spock.lang.Specification

class FunctionsSpec extends Specification {

    def "Right fold maintain order by combining the first element with the results of combining the rest"() {
        given:
        def concat = { a, b -> a + " " + b } as BiFunction
        def values = ["Hello", "world", "now", "foldr"]

        when:
        def res = Functions.foldRight(concat, "", values)

        then:
        res == "Hello world now foldr "
    }

    def "Left fold reverse order by recursively combining the results of combining all but the last element with the last one"() {
        given:
        def concat = { a, b -> a + " " + b } as BiFunction
        def values = ["Hello", "world", "now", "foldl"]

        when:
        def res = Functions.foldLeft(concat, "", values)

        then:
        res == "foldl now world Hello "
    }

    def "Head return first element of list"() {

        when:
        def res = Functions.head([1, 2])

        then:
        res == 1
    }

    def "Head fails on empty list"() {

        when:
        Functions.head([])

        then:
        thrown(IndexOutOfBoundsException)

    }

    def "Tail return all but first element of list"() {

        when:
        def res = Functions.tail(list)

        then:
        res == expected

        where:
        list      | expected
        [1, 2, 3] | [2, 3]
        [1]       | []

    }

    def "Tail fails on empty list"() {
        when:
        Functions.tail([])

        then:
        thrown(IllegalArgumentException)

    }

}
