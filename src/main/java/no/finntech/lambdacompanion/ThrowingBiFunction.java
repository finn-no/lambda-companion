package no.finntech.lambdacompanion;

@FunctionalInterface
public interface ThrowingBiFunction<T,R,S,E extends Throwable> {

    S apply(T t, R r) throws E;

}
