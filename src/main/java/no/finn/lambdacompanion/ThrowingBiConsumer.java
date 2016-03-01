package no.finn.lambdacompanion;

@FunctionalInterface
public interface ThrowingBiConsumer<T, R, E extends Exception> {

    void accept(T t, R r) throws E;

}
