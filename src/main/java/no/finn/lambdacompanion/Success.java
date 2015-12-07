package no.finn.lambdacompanion;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Success<T> extends Try<T> {

    private T t;

    public Success(T t) {
        this.t = t;
    }

    @Override
    public <U> Try<U> map(ThrowingFunction<? super T, ? extends U, ? extends Throwable> mapper) {
        try {
            return new Success<>(mapper.apply(t));
        } catch (Throwable e) {
            return new Failure<>(e);
        }
    }

    @Override
    public <U> Try<U> flatMap(ThrowingFunction<? super T, ? extends Try<U>, ? extends Throwable> mapper) {
        try {
            return mapper.apply(t);
        } catch (Throwable e) {
            return new Failure<>(e);
        }
    }

    @Override
    public void forEach(ThrowingConsumer<? super T, ? extends Throwable> consumer) {
        try {
            consumer.accept(t);
        } catch (Throwable ignore) {}
    }

    @Override
    public Try<T> peek(ThrowingConsumer<? super T, ? extends Throwable> consumer) {
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
                           Function<Throwable, ? extends U> failureFunc) {
        return successFunc.apply(t);
    }

    @Override
    public Either<? extends Throwable,T> toEither() {
        return Either.right(t);
    }

    @Override
    public <X extends Throwable, Y extends Throwable> T orElseThrow(Function<X, Y> throwableMapper) throws Y {
        return t;
    }

    @Override
    public <E extends Throwable> T orElseRethrow() throws E {
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
