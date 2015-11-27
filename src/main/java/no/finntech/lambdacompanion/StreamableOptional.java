package no.finntech.lambdacompanion;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Wrapper around an usual {@link java.util.Optional} that provides the very missed {@link #stream()} method useful for
 * flatMapping a {@link java.util.stream.Stream} of {@link java.util.Optional} (i.e. only keep present values).
 * <p>
 * example: the good
 * <pre>
 * {@code
 * collection.stream().flatMap(item -> StreamableOptional.ofNullable(item.getNullableProperty()).stream())
 * }
 * </pre>
 * would replace the bad:
 * <pre>
 * {@code
 * collection.stream().map(item -> Optional.ofNullable(item.getNullableProperty())).filter(Optional::isPresent).map(Optional::get)
 * }
 * </pre>
 * or the ugly:
 * <pre>
 * {@code
 * collection.stream().flatMap(item -> Optional.ofNullable(item.getNullableProperty()).map(Stream::of).orElseGet(Stream::empty))
 * }
 * </pre>
 */
public class StreamableOptional<T> {

    private static final StreamableOptional<?> EMPTY = new StreamableOptional(Optional.empty());

    private final Optional<T> optional;

    private StreamableOptional(final Optional<T> optional) {
        this.optional = optional;
    }

    /**
     * @see java.util.Optional#ifPresent(java.util.function.Consumer)
     */
    public void ifPresent(Consumer<? super T> consumer) {
        optional.ifPresent(consumer);
    }

    /**
     * @see java.util.Optional#filter(java.util.function.Predicate)
     */
    public StreamableOptional<T> filter(Predicate<? super T> predicate) {
        return optional.filter(predicate).map(present -> this).orElse(empty());
    }

    /**
     * @see java.util.Optional#map(java.util.function.Function)
     */
    public <U> StreamableOptional<U> map(Function<? super T, ? extends U> mapper) {
        return optional.map(mapper).map(StreamableOptional::of).orElse(empty());
    }

    /**
     * @see java.util.Optional#flatMap(java.util.function.Function)
     */
    public <U> StreamableOptional<U> flatMap(Function<? super T, StreamableOptional<U>> mapper) {
        return optional.map(present -> Objects.requireNonNull(mapper.apply(present))).orElse(empty());
    }

    /**
     * @see java.util.Optional#orElse(Object)
     */
    public T orElse(T other) {
        return optional.orElse(other);
    }

    /**
     * @see java.util.Optional#orElseGet(java.util.function.Supplier)
     */
    public T orElseGet(Supplier<? extends T> other) {
        return optional.orElseGet(other);
    }

    /**
     * @see java.util.Optional#orElseThrow(java.util.function.Supplier)
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return optional.orElseThrow(exceptionSupplier);
    }

    /**
     * Turns this optional into a {@link java.util.stream.Stream}
     *
     * @return a {@link java.util.stream.Stream} of the one value contained within this optional if it is present, an empty stream else
     */
    public Stream<T> stream() {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    /**
     * @return the {@link java.util.Optional} within
     */
    public Optional<T> toOptional() {
        return optional;
    }

    /**
     * @see java.util.Optional#of(Object)
     */
    public static <T> StreamableOptional<T> of(final T value) {
        return ofOptional(Optional.of(value));
    }

    /**
     * @see java.util.Optional#ofNullable(Object)
     */
    public static <T> StreamableOptional<T> ofNullable(final T value) {
        return ofOptional(Optional.ofNullable(value));
    }

    /**
     * Builds a StreamableOptional around the given {@link java.util.Optional}
     *
     * @param optional a regular {@link java.util.Optional}
     * @return a StreamableOptional
     */
    public static <T> StreamableOptional<T> ofOptional(final Optional<T> optional) {
        Objects.requireNonNull(optional);
        if (!optional.isPresent()) {
            return empty();
        }
        return new StreamableOptional<>(optional);
    }

    /**
     * @see java.util.Optional#empty()
     */
    public static <T> StreamableOptional<T> empty() {
        return (StreamableOptional<T>) EMPTY;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final StreamableOptional that = (StreamableOptional) o;

        if (!optional.equals(that.optional)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return optional.hashCode();
    }

}
