package no.finn.lambdacompanion;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {

    void accept(T t) throws E;

}
