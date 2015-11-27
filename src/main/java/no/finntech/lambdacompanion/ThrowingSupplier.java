package no.finntech.lambdacompanion;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Throwable> {

    T get() throws E;

}
