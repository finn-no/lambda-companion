package no.finntech.lambdacompanion;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Failure<T> extends Try<T> {

    private Throwable e;

    public Failure(Throwable e) {
        this.e = e;
    }

    public Throwable getThrowable() {
        return e;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Try<U> map(ThrowingFunction<? super T, ? extends U, ? extends Throwable> mapper) {
        return (Try<U>) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Try<U> flatMap(ThrowingFunction<? super T, ? extends Try<U>, ? extends Throwable> mapper) {
        return (Try<U>) this;
    }

    @Override
    public Optional<Try<T>> filter(final Predicate<T> predicate) {
        return Optional.of(this);
    }

    @Override
    public void forEach(ThrowingConsumer<? super T, ? extends Throwable> consumer) {}

    @Override
    public Try<T> peek(ThrowingConsumer<? super T, ? extends Throwable> consumer) {
        forEach(consumer);
        return this;
    }

    @Override
    public Try<T> peekFailure(Consumer<Failure<T>> consumer) {
        consumer.accept(this);
        return this;
    }

    @Override
    public T orElse(T defaultValue) {
        return defaultValue;
    }

    @Override
    public T orElseGet(Supplier<? extends T> defaultValue) {
        return defaultValue.get();
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.empty();
    }

    @Override
    public <U> U recover(Function<? super T, ? extends U> successFunc,
                           Function<Throwable, ? extends U> failureFunc) {
        return failureFunc.apply(e);
    }

    @Override
    public Either<? extends Throwable, T> toEither() {
        return Either.left(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X extends Throwable, Y extends Throwable> T orElseThrow(Function<X, Y> throwableMapper) throws Y {
        throw throwableMapper.apply((X) e);
    }

    @Override
    public <E extends Throwable> T orElseRethrow() throws E {
        throw (E) e;
    }

    @Override
    public String toString() {
        return "Failure{" +
                "e=" + e +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Failure<?> failure = (Failure<?>) o;

        return !(e != null ? !e.equals(failure.e) : failure.e != null);

    }

    @Override
    public int hashCode() {
        return e != null ? e.hashCode() : 0;
    }

}
