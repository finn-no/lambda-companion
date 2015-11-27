package no.finntech.lambdacompanion

import java.util.function.Supplier
import java.util.stream.Collectors

import spock.lang.Specification
import spock.lang.Unroll

class OptionalsSpec extends Specification {

    @Unroll
    def "test streamOf"() {
        when:
        def actual = Optionals.stream(opt).collect(Collectors.toList())
        then:
        actual == expected
        where:
        opt              || expected
        Optional.empty() || []
        Optional.of(42)  || [42]
    }

    def "test firstOf"() {
        given:
        def Supplier<Optional<String>> FIRST = Mock(Supplier)
        1 * FIRST.get() >> Optional.empty()
        def Supplier<Optional<String>> SECND = Mock(Supplier)
        1 * SECND.get() >> Optional.of("second")
        def Supplier<Optional<String>> THIRD = Mock(Supplier)
        0 * THIRD.get() >> Optional.of("third")
        when:
        def actual = Optionals.firstOf(FIRST, SECND, THIRD)
        then:
        actual == Optional.of("second")
    }

    @Unroll("string '#string' should result in #expected")
    def "test ofBlankable"() {
        expect:
        Optionals.ofBlankable(string) == expected
        where:
        string || expected
        null   || Optional.empty()
        ""     || Optional.empty()
        "    " || Optional.empty()
        "test" || Optional.of("test")
    }

    @Unroll("#optional filtered with class String should be #expected")
    def "test filter by class"() {
        expect:
        Optionals.filter(optional, String.class) == expected
        where:
        optional            || expected
        Optional.of("test") || Optional.of("test")
        Optional.of(42)     || Optional.empty()
    }

}