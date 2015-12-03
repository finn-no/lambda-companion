package no.finntech.lambdacompanion;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class Optionals {

    private Optionals() {
    }

    /**
     * Out of a list of suppliers of optional values, returns the first one that is present without unnecessary evaluating
     * the next suppliers.
     * Ex:
     * <pre>
     * {@code
     *     Optional first = Optionals.firstOf(() -> firstCostyOperation()
     *                                        () -> secondCostlyOperation()
     *                                        () -> thirdCostlyOperation());
     * }
     * </pre>
     * In case firstCostyOperation() returns an Optional.empty(), then secondCostlyOperation() will be invoked. If secondCostlyOperation()
     * returns a present Optional, then thirdCostlyOperation() won't be invoked.
     * @param suppliers for methods that need to be conditionnally invoked
     * @param <T> t
     * @return the first supplied Optional that is present
     */
    public static <T> Optional<T> firstOf(final Supplier<Optional<T>>... suppliers) {
        return Stream.of(suppliers).map(Supplier::get).flatMap(Optionals::stream).findFirst();
    }

    /**
     * Convenience method to turn an Optional into a Stream, due to the lack Optional#stream() method...
     * <pre>
     * {@code
     *     List<Optional<String>> listOfOptionals = //...
     *     List<String> strings = list.stream().flatMap(Optionals::stream).collect(toList());
     * }
     * </pre>
     * @param optional optional
     * @param <T> t
     * @return a Stream of 1 element or an empty Stream
     */
    public static <T> Stream<T> stream(final Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    /**
     * Convenience method to filter an optional based on its class and cast it at the same time
     * @param opt opt
     * @param cls cls
     * @param <T> T
     * @return a present Optional casted to T if present and matching filtering class, else Optional#empty()
     */
    public static <T> Optional<T> filter(final Optional<?> opt, final Class<T> cls) {
        return (Optional<T>) opt.filter(cls::isInstance);
    }

    /**
     * Convenience method to build and Optional out of a String that can be null or blank
     * @param blankable blankable
     * @return a present Optional of the string if not null and not blank, else Optional#empty()
     */
    public static Optional<String> ofBlankable(final String blankable) {
        return Optional.ofNullable(blankable).filter(maybeEmpty -> !maybeEmpty.trim().isEmpty());
    }

}
