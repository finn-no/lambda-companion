package no.finn.lambdacompanion;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Deprecated
public class Success<T> extends Try<T> {

    private T t;

    public Success(T t) {
        this.t = t;
    }

    @Override
    public <U> Try<U> map(ThrowingFunction<? super T, ? extends U, ? extends Exception> mapper) {
        try {
            return new Success<>(mapper.apply(t));
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }

    @Override
    public <U> Try<U> flatMap(ThrowingFunction<? super T, ? extends Try<U>, ? extends Exception> mapper) {
        try {
            return mapper.apply(t);
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }

    @Override
    public Optional<Try<T>> filter(final Predicate<T> predicate) {
        Objects.requireNonNull(predicate);
        if (predicate.test(t)) {
            return Optional.of(this);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void forEach(ThrowingConsumer<? super T, ? extends Exception> consumer) {
        try {
            consumer.accept(t);
        } catch (Exception ignore) {}
    }

    @Override
    public Try<T> peek(ThrowingConsumer<? super T, ? extends Exception> consumer) {
        forEach(consumer);
        return this;
    }

    @Override
    public Try<T> peekFailure(Consumer<Failure<T>> consumer) {
        return this;
    }

    @Override
    public T orElse(T defaultValue) {
        return t;
    }

    @Override
    public T orElseGet(Supplier<? extends T> defaultValue) {
        return t;
    }

    @Override
    public Optional<T> toOptional() {
        return Optional.ofNullable(t);
    }

    @Override
    public <U> U recover(Function<? super T, ? extends U> successFunc,
                           Function<Exception, ? extends U> failureFunc) {
        return successFunc.apply(t);
    }

    @Override
    public Either<? extends Exception,T> toEither() {
        return Either.right(t);
    }

    @Override
    public <X extends Exception, Y extends Exception> T orElseThrow(Function<X, Y> ExceptionMapper) throws Y {
        return t;
    }

    @Override
    public <E extends Exception> T orElseRethrow() throws E {
        return t;
    }

    @Override
    public String toString() {
        return "Success{" +
                "t=" + t +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Success<?> success = (Success<?>) o;

        return !(t != null ? !t.equals(success.t) : success.t != null);

    }

    @Override
    public int hashCode() {
        return t != null ? t.hashCode() : 0;
    }

}
