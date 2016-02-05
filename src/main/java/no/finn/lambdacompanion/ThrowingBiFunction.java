package no.finn.lambdacompanion;

@FunctionalInterface
public interface ThrowingBiFunction<T,R,S,E extends Exception> {

    S apply(T t, R r) throws E;

}
