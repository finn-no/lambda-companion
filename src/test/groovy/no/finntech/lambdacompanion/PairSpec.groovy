package no.finntech.lambdacompanion

import java.util.stream.Stream

import spock.lang.Specification

class PairSpec extends Specification {

    def "toMap collector collects Pair-s into a map"() {
        given:
        def stream = Stream.of(new Pair<>("first", 1), new Pair<>("second", 2))
        when:
        def map = stream.collect(Pair.toMap())
        then:
        map == ["first":1, "second":2]
    }

    def "a pair turns into an entry"() {
        given:
        def key = "Key"
        def value = 42
        def pair = new Pair<>(key, value)
        when:
        def entry = pair.toMapEntry()
        then:
        entry.getKey() == key
        entry.getValue() == value
    }

    def "test equals"() {
        expect:
        new Pair("left", "right").equals(new Pair("left", "right"))
    }
    
}