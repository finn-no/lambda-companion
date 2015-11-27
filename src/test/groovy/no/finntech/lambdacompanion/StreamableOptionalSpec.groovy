package no.finntech.lambdacompanion

import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

import spock.lang.Specification

class StreamableOptionalSpec extends Specification {

    def "building a streamable optional out of a null value fails"() {
        when:
        StreamableOptional.of(null)
        then:
        thrown NullPointerException
    }

    def "building a streamable optional out of a null optional fails"() {
        when:
        StreamableOptional.ofOptional(null)
        then:
        thrown NullPointerException
    }

    def "building a streamable optional out of an empty optional returns an empty streamble optional"() {
        when:
        def opt = StreamableOptional.ofOptional(Optional.empty())
        then:
        opt.is(StreamableOptional.empty())
    }

    def "empty streamable optional returns an empty stream"() {
        given:
        def opt = StreamableOptional.empty()
        when:
        def stream = opt.stream();
        then:
        stream.count() == 0
    }

    def "streamable optional of a null value returns an empty stream"() {
        given:
        def opt = StreamableOptional.ofNullable(null)
        when:
        def stream = opt.stream();
        then:
        stream.count() == 0
    }

    def "streamable optional of a non-null value returns a stream of one"() {
        given:
        def opt = StreamableOptional.of(42)
        when:
        def stream = opt.stream();
        then:
        stream.collect(Collectors.toList()) == [42]
    }

    def "filter returns the same streamable optional when predicate turns out true"() {
        given:
        def opt = StreamableOptional.of(42)
        when:
        def actual = opt.filter(new Predicate<Integer>() {
            boolean test(final Integer integer) {
                integer == 42
            }
        })
        then:
        actual.is(opt)
    }

    def "filter returns the an empty streamable optional when predicate turns out false"() {
        given:
        def opt = StreamableOptional.of(42)
        when:
        def actual = opt.filter(new Predicate<Integer>() {
            boolean test(final Integer integer) {
                integer != 42
            }
        })
        then:
        actual == StreamableOptional.empty()
    }

    def "map returns the same streamable optional when it was an empty streamable optional"() {
        given:
        def opt = StreamableOptional.<Integer>empty()
        when:
        def actual = opt.map(new Function<Integer, String>() {
            String apply(final Integer integer) {
                integer.toString()
            }
        })
        then:
        actual.is(opt)
    }

    def "map returns a streamable optional when it was an non-empty streamable optional"() {
        given:
        def opt = StreamableOptional.of(42)
        when:
        def actual = opt.map(new Function<Integer, String>() {
            String apply(final Integer integer) {
                integer.toString()
            }
        }).orElse("")
        then:
        actual == "42"
    }

    def "flatMap returns the same streamable optional when it was an empty streamable optional"() {
        given:
        def opt = StreamableOptional.<Integer>empty()
        when:
        def actual = opt.flatMap(new Function<Integer, StreamableOptional<String>>() {
            StreamableOptional<String> apply(final Integer integer) {
                StreamableOptional.of(integer.toString())
            }
        })
        then:
        actual.is(opt)
    }

    def "flatMap returns a streamable optional when it was a non-empty streamable optional"() {
        given:
        def opt = StreamableOptional.<Integer> empty()
        when:
        def actual = opt.flatMap(new Function<Integer, StreamableOptional<String>>() {

            StreamableOptional<String> apply(final Integer integer) {
                StreamableOptional.of(integer.toString())
            }
        })
        then:
        actual.is(opt)
    }

}