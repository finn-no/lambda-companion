package no.finntech.lambdacompanion

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

import spock.lang.Specification
import spock.lang.Unroll

class EitherSpec extends Specification {

    def "If Either is a Left, isLeft should return true" () {
        given:
        final Either<Integer, String> either = Either.left(Integer.MAX_VALUE)
        when:
        def actual = either.isLeft();
        then:
        actual
    }

    def "If Either is a Right, isLeft should return false" () {
        given:
        final Either<Integer, String> either = Either.right("string")
        when:
        def actual = either.isLeft();
        then:
        !actual
    }

    def "If Either is a Left left projection gets the left value" () {
        given:
        def expected = Integer.MAX_VALUE
        final Either<Integer, String> either = Either.left(expected)
        when:
        def actual = either.left().orElse(0);
        then:
        actual == expected
    }

    def "If Either is a Left left projection supplies the left value" () {
        given:
        def expected = Integer.MAX_VALUE
        final Either<Integer, String> either = Either.left(expected)
        when:
        def actual = either.left().orElseGet(new Supplier<Integer>() {
            Integer get() {
                return 0;
            }
        })
        then:
        actual == expected
    }

    def "If Either is a Right left projection gets the default value" () {
        given:
        def expected = ""
        final Either<String, Integer> either = Either.right(Integer.MAX_VALUE)
        when:
        def actual = either.left().orElse(expected)
        then:
        actual == expected
    }

    def "If Either is a Right left projection supplies the default value" () {
        given:
        def expected = ""
        final Either<String, Integer> either = Either.right(Integer.MAX_VALUE)
        when:
        def actual = either.left().orElseGet(new Supplier<String>() {
            String get() {
                return expected;
            }
        })
        then:
        actual == expected
    }

    def "If Either is a Right right projection gets the right value" () {
        given:
        def expected = Integer.MAX_VALUE
        final Either<String, Integer> either = Either.right(expected)
        when:
        def actual = either.right().orElse(0)
        then:
        actual == expected
    }

    def "If Either is a Right right projection supplies the right value" () {
        given:
        def expected = Integer.MAX_VALUE
        final Either<String, Integer> either = Either.right(expected)
        when:
        def actual = either.right().orElseGet(new Supplier<Integer>() {
            Integer get() {
                return 0;
            }
        })
        then:
        actual == expected
    }

    def "If Either is a Left right projection gets the default value" () {
        given:
        def expected = ""
        final Either<Integer, String> either = Either.left(Integer.MAX_VALUE)
        when:
        def actual = either.right().orElse(expected)
        then:
        actual == expected
    }

    def "If Either is a Left right projection supplies the default value" () {
        given:
        def expected = ""
        final Either<Integer, String> either = Either.left(Integer.MAX_VALUE)
        when:
        def actual = either.right().orElseGet(new Supplier<String>() {
            String get() {
                return expected;
            }
        })
        then:
        actual == expected
    }

    def "If Either is a Right right projection maps the right value" () {
        given:
        def expected = 0
        final Either<String, Integer> either = Either.right(Integer.MAX_VALUE)
        when:
        def actual = either.right().map(new Function<Integer, Integer>() {
                                    Integer apply(final Integer input) {
                                        return expected
                                    }
                                })
        then:
        actual instanceof Either.Right
        actual.value == expected
    }

    def "If Either is a Left left projection maps the left value" () {
        given:
        def expected = 0
        final Either<Integer, String> either = Either.left(Integer.MAX_VALUE)
        when:
        def actual = either.left().map(new Function<Integer, Integer>() {
                                    Integer apply(final Integer input) {
                                        return expected
                                    }
                                })
        then:
        actual instanceof Either.Left
        actual.value == expected
    }

    def "If Either is a Left right projection map keeps the left value" () {
        given:
        def expected = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.left(expected)
        when:
        def actual = either.right().map(new Function<Integer, Integer>() {
            Integer apply(final Integer input) {
                return 0
            }
        })
        then:
        actual instanceof Either.Left
        actual.value == expected
    }

    def "If Either is a Right left projection map keeps the right value" () {
        given:
        def expected = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.right(expected)
        when:
        def actual = either.left().map(new Function<Integer, Integer>() {
            Integer apply(final Integer input) {
                return 0
            }
        })
        then:
        actual instanceof Either.Right
        actual.value == expected
    }

    def "If Either is a Left should left join the left value" () {
        given:
        def expected = 0
        final Either<Integer, String> either = Either.left(Integer.MAX_VALUE)
        when:
        def actual = either.joinLeft(new Function<Integer, Either<Integer, String>>() {
            Either<Integer, String> apply(final Integer input) {
                return Either.left(expected)
            }
        })
        then:
        actual instanceof Either.Left
        actual.value == expected
    }

    def "If Either is a Right left join keeps the right value" () {
        given:
        def expected = Integer.MAX_VALUE
        final Either<String, Integer> either = Either.right(expected)
        when:
        def actual = either.joinLeft(new Function<String, Either<String, Integer>>() {
            Either<String, Integer> apply(final String input) {
                return Either.left("")
            }
        })
        then:
        actual instanceof Either.Right
        actual.value == expected
    }

    def "If Either is a Right should right join the right value" () {
        given:
        def expected = 0
        final Either<String, Integer> either = Either.right(Integer.MAX_VALUE)
        when:
        def actual = either.joinRight(new Function<Integer, Either<String, Integer>>() {
            Either<String, Integer> apply(final Integer input) {
                return Either.right(expected)
            }
        })
        then:
        actual instanceof Either.Right
        actual.value == expected
    }

    def "If Either is a Left right join keeps the left value" () {
        given:
        def expected = Integer.MAX_VALUE
        final Either<Integer, String> either = Either.left(expected)
        when:
        def actual = either.joinRight(new Function<String, Either<Integer, String>>() {
            Either<String, Integer> apply(final String input) {
                return Either.left("")
            }
        })
        then:
        actual instanceof Either.Left
        actual.value == expected
    }

    def "If Either is a Left left projection returns an Optional of the Left value"() {
        given:
        def expected = Integer.MAX_VALUE
        final Either<Integer, String> either = Either.left(expected)
        when:
        def opt = either.left().toOptional()
        then:
        opt.isPresent()
        opt.orElse(0) == expected
    }

    def "If Either is a Right left projection returns an Optional.Absent"() {
        given:
        def expected = Integer.MAX_VALUE
        final Either<String, Integer> either = Either.right(expected)
        when:
        def opt = either.left().toOptional()
        then:
        !opt.isPresent()
    }

    def "If Either is a Right right projection returns an Optional of the Right value"() {
        given:
        def expected = Integer.MAX_VALUE
        final Either<String, Integer> either = Either.right(expected)
        when:
        def opt = either.right().toOptional()
        then:
        opt.isPresent()
        opt.orElse(0) == expected
    }

    def "If Either is a Left right projection returns an Optional.Absent"() {
        given:
        def expected = Integer.MAX_VALUE
        final Either<Integer, String> either = Either.left(expected)
        when:
        def opt = either.right().toOptional()
        then:
        !opt.isPresent()
    }

    def "If Either is a Right right projection executes the side effect"() {
        given:
        def value = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.right(value)
        final Consumer<Integer> consumer = Mock(Consumer)
        when:
        either.right().forEach(consumer)
        then:
        1 * consumer.accept(value)
    }

    def "If Either is a Left right projection does not execute the side effect"() {
        given:
        def value = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.left(value)
        final Consumer<Integer> consumer = Mock(Consumer)
        when:
        either.right().forEach(consumer)
        then:
        0 * consumer.accept(value)
    }

    def "If Either is a Left left projection executes the side effect"() {
        given:
        def value = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.left(value)
        final Consumer<Integer> consumer = Mock(Consumer)
        when:
        either.left().forEach(consumer)
        then:
        1 * consumer.accept(value)
    }

    def "If Either is a Right left projection does not execute the side effect"() {
        given:
        def value = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.right(value)
        final Consumer<Integer> consumer = Mock(Consumer)
        when:
        either.left().forEach(consumer)
        then:
        0 * consumer.accept(value)
    }

    def "If Either is a Right right projection peeks the side effect"() {
        given:
        def value = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.right(value)
        final Consumer<Integer> consumer = Mock(Consumer)
        when:
        either.right().peek(consumer)
        then:
        1 * consumer.accept(value)
    }

    def "If Either is a Left right projection does not peek the side effect"() {
        given:
        def value = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.left(value)
        final Consumer<Integer> consumer = Mock(Consumer)
        when:
        either.right().peek(consumer)
        then:
        0 * consumer.accept(value)
    }

    def "If Either is a Left left projection peeks the side effect"() {
        given:
        def value = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.left(value)
        final Consumer<Integer> consumer = Mock(Consumer)
        when:
        either.left().peek(consumer)
        then:
        1 * consumer.accept(value)
    }

    def "If Either is a Right left projection does not peek the side effect"() {
        given:
        def value = Integer.MAX_VALUE
        final Either<Integer, Integer> either = Either.right(value)
        final Consumer<Integer> consumer = Mock(Consumer)
        when:
        either.left().peek(consumer)
        then:
        0 * consumer.accept(value)
    }

    def "A Left is not equal to a Right "() {
        given:
        def value = "a"
        final Either<String, String> left = Either.left(value)
        final Either<String, String> right = Either.right(value)
        when:
        final boolean isEqual = left.equals(right)
        then:
        !isEqual
    }

    def "A Right is not equal to a Left "() {
        given:
        def value = "a"
        final Either<String, String> left = Either.left(value)
        final Either<String, String> right = Either.right(value)
        when:
        final boolean isEqual = right.equals(left)
        then:
        !isEqual
    }

    def "A Right can be converted to 'exception flow'"() {
        given:
        final Either<String, Integer> either = Either.left("failure")
        when:
        either.right().orElseThrow({ failure -> new Exception(failure) })
        then:
        thrown(Exception)
    }

    def "A Left can be converted to 'exception flow'"() {
        given:
        final Either<Integer, String> either = Either.right("failure")
        when:
        either.left().orElseThrow({ failure -> new Exception(failure) })
        then:
        thrown(Exception)
    }

    def "If Either is a Left left projection gets the left value when exception fallback is defined"() {
        given:
        final Either<Integer, String> either = Either.left(Integer.MAX_VALUE)
        when:
        def outcome = either.left().orElseThrow({ failure -> new Exception(failure) })
        then:
        notThrown(Exception)
        and:
        outcome.equals(Integer.MAX_VALUE)
    }

    def "If Either is a Right right projection gets the right value when exception fallback is defined"() {
        given:
        final Either<String, Integer> either = Either.right(Integer.MAX_VALUE)
        when:
        def outcome = either.right().orElseThrow({ failure -> new Exception(failure) })
        then:
        notThrown(Exception)
        and:
        outcome.equals(Integer.MAX_VALUE)
    }

    @Unroll
    def "Lefts with equal values are equal"() {
        given:
        final Either<String, String> left1 = Either.left(value)
        final Either<String, String> left2 = Either.left(value)
        expect:
        left1.equals(left2)
        where:
        value << [null, "a"]
    }

    @Unroll
    def "Rights with equal values are equal"() {
        given:
        final Either<String, String> right1 = Either.right(value)
        final Either<String, String> right2 = Either.right(value)
        expect:
        right1.equals(right2)
        where:
        value << [null, "a"]
    }

    def "Lefts with non-null values have hash codes equal to their corresponding values"() {
        given:
        def value = "a"
        final Either<String, String> left = Either.left(value)
        when:
        final int hashCode = left.hashCode()
        then:
        hashCode == value.hashCode()
    }

    def "Rights with non-null values have hash codes equal to their corresponding values"() {
        given:
        def value = "a"
        final Either<String, String> right = Either.right(value)
        when:
        final int hashCode = right.hashCode()
        then:
        hashCode == value.hashCode()
    }

    def "Lefts with null values have hash codes equal to 0" () {
        given:
        final Either<String, String> left = Either.left(null)
        when:
        final int hashCode = left.hashCode()
        then:
        hashCode == 0
    }

    def "Rights with null values have hash codes equal to 0" () {
        given:
        final Either<String, String> right = Either.right(null)
        when:
        final int hashCode = right.hashCode()
        then:
        hashCode == 0
    }

    def "toString describes Lefts with non-null values properly"() {
        given:
        def value = "a"
        final Either<String, String> left = Either.left(value)
        when:
        final String string = left.toString()
        then:
        string == "Left(" + value + ")"
    }

    def "toString describes Rights with non-null values properly"() {
        given:
        def value = "a"
        final Either<String, String> right = Either.right(value)
        when:
        final String string = right.toString()
        then:
        string == "Right(" + value + ")"
    }

    def "toString describes Lefts with null values properly"() {
        given:
        final Either<String, String> left = Either.left(null)
        when:
        final String string = left.toString()
        then:
        string == "Left(null)"
    }

    def "toString describes Rights with null values properly"() {
        given:
        final Either<String, String> right = Either.right(null)
        when:
        final String string = right.toString()
        then:
        string == "Right(null)"
    }

    def "null values in Either don't crash it on right side"() {
        given:
        final Either<String, String> right = Either.right(null)
        when:
        def res = right.right().map(new Function<String, String>() {
                                        String apply(final String s) {
                                            return "this string is "+s
                                        }
                                    })
        then:
        res.fold(new Function<String, String>() {
                     String apply(final String s) {
                         return ""
                     }
                 },
                 Function.identity()) == "this string is null"
    }

    def "null values in Either don't crash it on left side"() {
        given:
        final Either<String, String> left = Either.left(null)
        when:
        def res = left.left().map(new Function<String, String>() {
                                        String apply(final String s) {
                                            return "this string is "+s
                                        }
                                    })
        then:
        res.fold(Function.identity(),
                 new Function<String, String>() {
                     String apply(final String s) {
                         return ""
                     }
                 }) == "this string is null"
    }

    def "null values in Either don't crash it on right side even when it is a Left"() {
        given:
        final Either<String, String> left = Either.left(null)
        when:
        def res = left.right().map(new Function<String, String>() {
                                        String apply(final String s) {
                                            return "this string is "+s
                                        }
                                    })
        then:
        res.fold(Function.identity(), Function.identity()) == null
    }

    def "null values in Either don't crash it on left side even when it is a Right"() {
        given:
        final Either<String, String> right = Either.right(null)
        when:
        def res = right.left().map(new Function<String, String>() {
                                        String apply(final String s) {
                                            return "this string is "+s
                                        }
                                    })
        then:
        res.fold(Function.identity(), Function.identity()) == null
    }

}
