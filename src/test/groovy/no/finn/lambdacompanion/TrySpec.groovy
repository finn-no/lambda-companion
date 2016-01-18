package no.finn.lambdacompanion

import spock.lang.Specification

class TrySpec extends Specification {

    def "If Try is a Success, a successful map should yield new success"() {
        given:
        def myTry = new Success<>("3")
        when:
        def actual = myTry.map({ String s -> Integer.parseInt(s) })
        then:
        actual instanceof Success<Integer>
    }

    def "If Try is a Success, a failing map should yield failure"() {
        given:
        def myTry = new Success<>("blargh")
        when:
        def actual = myTry.map({ String s -> Integer.parseInt(s) })
        then:
        actual instanceof Failure
    }

    def "If Try is a Success, a failing flatMap should yield failure"() {
        given:
        def myTry = new Success<>("blargh")
        when:
        def actual = myTry.flatMap({ s -> new Failure<>(new RuntimeException("asdf")) })
        then:
        actual instanceof Failure
    }

    def "if Try is a Success, a oElse should get the value, not the fallback value"() {
        given:
        def myTry = new Success<>("blargh")
        when:
        def actual = myTry.orElse("somethingElse")
        then:
        actual == "blargh"
    }

    def "if Try is a Failure, a orElse should get the fallback value"() {
        given:
        def myTry = new Failure<>(new RuntimeException("Asdf"))
        when:
        def actual = myTry.orElse("somethingElse")
        then:
        actual == "somethingElse"
    }

    def "if Try is a Success, a orElseGet should get the success value"() {
        given:
        def myTry = new Success<>("blargh")
        when:
        def actual = myTry.orElseGet({ -> "roargh" })
        then:
        actual == "blargh"
    }

    def "if Try is a Failure, a orElseGet should get a fallback supplier value"() {
        given:
        def myTry = new Failure<>(new IllegalArgumentException("Nooot good"))
        when:
        def actual = myTry.orElseGet({ -> "roargh" })
        then:
        actual == "roargh"
    }

    def "if Try is a Success, toOptional should give an Optional around the value"() {
        given:
        def myTry = new Success<>("yoyo")
        when:
        def actual = myTry.toOptional()
        then:
        actual.get() == "yoyo"
    }

    def "if Try is a Failure, toOptional should give empty Optional"() {
        given:
        def myTry = new Failure<>(new Exception(":x"))
        when:
        def actual = myTry.toOptional()
        then:
        !actual.isPresent()
    }

    def "if Try is a Success, recover should run the success function"() {
        given:
        def myTry = new Success<>("hello")
        when:
        def actual = myTry.recover({ s -> s + " world" },
                { e -> "aaw, that's ok, world." })
        then:
        actual == "hello world"
    }

    def "If Try is a Failure, fold should give a fallback Success"() {
        given:
        def myTry = new Failure<>(new RuntimeException(":("))
        when:
        def actual = myTry.recover({ s -> " world" },
                { e -> "aaw, that's ok, world." })
        then:
        actual == "aaw, that's ok, world."
    }

    def "If Try is a Success, toEither should give an Either.right around the value"() {
        given:
        def myTry = new Failure<>(new ConcurrentModificationException(":o"))
        when:
        def actual = myTry.toEither().fold({ i -> i }, { i -> i })
        then:
        actual instanceof ConcurrentModificationException
    }

    def "peek should return a Try unchanged"() {
        given:
        def myTry = new Failure(new ArrayIndexOutOfBoundsException(":o"))
        when:
        def actual = myTry.peek({ f -> System.out.println("something") })
        then:
        actual.recover({ s -> s }, { f -> f instanceof ArrayIndexOutOfBoundsException ? 1 : 0 }) == 1
    }

    def "peekFailure should return a Try unchanged"() {
        given:
        def myTry = new Failure(new ArrayIndexOutOfBoundsException(":o"))
        when:
        def actual = myTry.peekFailure({ f -> System.out.println("something") })
        then:
        actual.recover({ s -> s }, { f -> f instanceof ArrayIndexOutOfBoundsException ? 1 : 0 }) == 1
    }

    def "should create a Try from a ThrowingFunction"() {
        given:
        def myTry = Try.of({ i -> new Integer(i) }, 3);
        when:
        def actual = myTry
        then:
        actual instanceof Try
    }

    def "should create a Try from a ThrowingBiFunction"() {
        given:
        def myTry = Try.of({ a, b -> a / b }, 1, 0)
        when:
        def actual = myTry
        then:
        actual instanceof Try
    }

    def "should create a Try from a ThrowingSupplier"() {
        given:
        def myTry = Try.of({ -> new String("") })
        when:
        def actual = myTry
        then:
        actual instanceof Try
    }

    def "should get value from orElseThrow if success"() {
        given:
        def myTry = Try.of({ -> new String("fish") })
        when:
        def actual = myTry.orElseThrow({ e -> e })
        then:
        actual == "fish"
    }

    def "should throw exception mapped through provided function when orElseThrow on a Failure"() {
        given:
        def myTry = new Failure(new IOException("floppy drive way too busy"))
        when:
        myTry.orElseThrow({ e -> e })
        then:
        thrown(IOException)
    }

    def "should get value from orElseRethrow if success"() {
        given:
        def myTry = Try.of({ -> new String("fish") })
        when:
        def actual = myTry.orElseRethrow()
        then:
        actual == "fish"
    }

    def "should throw caught exception when orElseRethrow on a Failure"() {
        given:
        def myTry = new Failure(new IOException("floppy drive way too busy"))
        when:
        myTry.orElseRethrow()
        then:
        thrown(IOException)
    }

    def "should sequence a list of successes to a try of list" () {
        given:
        def successes = Arrays.asList(new Success<>("yo"), new Success<>("dude"), new Success<>("hmm"))
        when:
        def actual = Try.sequence(successes).orElse(Collections.emptyList())
        then:
        actual.get(0) == "yo"
        actual.get(1) == "dude"
        actual.get(2) == "hmm"
    }

    def "should sequence a list containing a failure to first failure in list" () {
        given:
        def tries = Arrays.asList(new Success<>("yo"), new Failure<>(new IllegalFormatCodePointException(3)))
        when:
        Try.sequence(tries).orElseRethrow()
        then:
        thrown(IllegalFormatCodePointException)
    }

    def "should give illegalargument failure on sequencing empty list" () {
        given:
        def tries = Collections.emptyList()
        when:
        Try.sequence(tries).orElseRethrow()
        then:
        thrown(IllegalArgumentException)
    }



}
