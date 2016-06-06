package no.finn.lambdacompanion;

@FunctionalInterface
public interface ThrowingVoidSupplier<E extends Exception> {

    void get() throws E;
}
