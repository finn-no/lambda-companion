package no.finn.lambdacompanion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Try is a right-biased datatype for wrapping function calls that might fail with an Exception.
 *
 * A Try is alwyas Success or Failure. Success holds a value, and Failure holds an Exception.
 *
 * Being right biased, you can map and flatMap on it successivly, delaying handling of failure to
 * the very end of your chain.
 *
 * the recover-method usually comes at the end of a call-chain and forces the handling of both
 * success and failure.
 *
 * @param <T> t
 */
public abstract class Try<T> {

    /**
     * Applies a function on a value of type Success. Returns self if Failure.
     * @param mapper Function from T to U with an Exception in the signature
     * @param <U> Type of the return value of the function
     * @return a new Try
     */
    public abstract <U> Try<U> map(ThrowingFunction<? super T, ? extends U, ? extends Exception> mapper);

    /**
     * Applies a function on two values. Returns Success of the resulting value, or Returns self if Failure.
     * Same as map() but needs a function ending in a Try
     * @param mapper Function from T to Try&lt;U&gt; with an Exception in the signature
     * @param <U> Type of the value to be wrapped in a Try of the function
     * @return a new Try
     */
    public abstract <U> Try<U> flatMap(ThrowingFunction<? super T, ? extends Try<U>, ? extends Exception> mapper);

    /**
     * Applies a filter, where a match returns Success and Failure otherwise.
     * @param predicate Predicate function to determine Success or Failure
     * @return the same or new try
     */
    public abstract Optional<Try<T>> filter(Predicate<T> predicate);

    /**
     * Accepts a consuming function and applies it to the value if it is a Success. Does nothing if Failure.
     * @param consumer Consuming function with an Exception in the signature
     */
    public abstract void forEach(ThrowingConsumer<? super T, ? extends Exception> consumer);

    /**
     * Same as forEach but returns the Try for further chaining
     * @param consumer Consuming function with an Exception in the signature
     * @return the same Try
     */
    public abstract Try<T> peek(ThrowingConsumer<? super T, ? extends Exception> consumer);

    /**
     * Does nothing on Success, but accepts a consumer on Failure
     * @param consumer Consuming function with a failure
     * @return the same Try
     */
    public abstract Try<T> peekFailure(Consumer<Failure<T>> consumer);

    /**
     * Returns the value of the Success, or a default value of the same type if this is a Failure.
     * Note that the argument is always evaluated, and this is not lazy (as opposed to orElseGet)
     * @param defaultValue default fallback value
     * @return value
     */
    public abstract T orElse(T defaultValue);

    /**
     * Returns the value of the Success, or lazily falls back to a supplied value of the same type
     * @param defaultValue lazy default supplier of fallback value
     * @return value
     */
    public abstract T orElseGet(Supplier<? extends T> defaultValue);

    /**
     * Accepts two functions, the first applied if Success - returning the value,
     * the other executed if Failure, returning a fallback value. Note that
     * - the first function cannot have an Exception in its signature.
     * - the fallback function must end in a value
     * @param successFunc Function handling the Success case
     * @param failureFunc Function handling the Failure case
     * @param <U> Type of the value
     * @return a value of type U
     */
    public abstract <U> U recover(Function<? super T, ? extends U> successFunc,
                                    Function<Exception, ? extends U> failureFunc);

    /**
     * Creates an Optional wrapping the value if Success. Creates an empty Optional if Failure.
     * @return An Optional
     */
    public abstract Optional<T> toOptional();

    /**
     * Creates an Either where the Left is the Failure and Right is the Success from this Try
     * @param <X> Exception
     * @return an Either
     */
    public abstract <X extends Exception> Either<X,T> toEither();

    /**
     * Escapes the Try and enters a regular try-catch flow
     * @param ExceptionMapper Function to transform the Exception if this is a Failure
     * @param <X> any Exception
     * @param <Y> any Exception
     * @return Value or a transformed Exception
     * @throws Y any Exception
     */
    public abstract <X extends Exception, Y extends Exception> T orElseThrow(Function<X, Y> ExceptionMapper) throws Y;

    /**
     * Escapes the Try and enters a regular try-catch flow by rethowing the caught exception when a Failure
     * @param <E> any Exception
     * @return Value or a transformed Exception
     * @throws E any Exception
     */
    public abstract <E extends Exception> T orElseRethrow() throws E;


    /**
     * Starting point to the Try structure. Create a try from a function that throws an Exception
     * and an argument to this function
     * @param func Function to be attempted, e.g. URL::new
     * @param v Argument for the function
     * @param <U> Type of the function return value
     * @param <V> Type of the function argument
     * @return a Try
     */
    public static <U,V> Try<U> of(ThrowingFunction<V, ? extends U, ? extends Exception> func, V v) {
        try {
            return new Success<>(func.apply(v));
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }

    /**
     * Starting point to the Try structure. Create a try from a function that throws an Exception
     * and two arguments to this function
     * @param func Function to be attempted, e.g. (a,b) -&gt; a / b
     * @param v First argument for the function
     * @param w Second argument for the function
     * @param <U> Type of the function return value
     * @param <V> Type of the first function argument
     * @param <W> Type of the second function argument
     * @return a Try
     */
    public static <U,V,W> Try<U> of(ThrowingBiFunction<V, W, ? extends U, ? extends Exception> func, V v, W w) {
        try {
            return new Success<>(func.apply(v, w));
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }

    /**
     * Starting point to the Try structure. Create a try from a Supplier that throws an Exception
     * @param supplier The supplier function
     * @param <U> Type of the supplied object from the supplier function
     * @return a Try
     */
    public static <U> Try<U> of(ThrowingSupplier<U, ? extends Exception> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Exception e) {
            return new Failure<>(e);
        }
    }

    public static <U> Try<U> failure(Exception Exception) {
        return new Failure<>(Exception);
    }

    public static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates one Try from a list of tries containing the same type, or the _first_ failure in the given list
     * @param tries List of tries
     * @param <T> the type
     * @return One Try containing a list of Ts
     */
    public static <T> Try<List<T>> sequence(List<Try<T>> tries) {
        if (tries.size() == 0) {
            return Try.failure(new IllegalArgumentException("Cannot sequence an empty list"));
        }
        Try<List<T>> head = Functions.head(tries).map(Collections::singletonList);
        if (tries.size() == 1 || !head.toOptional().isPresent()) {
            return head;
        }
        return concat(head, head.flatMap(t -> sequence(Functions.tail(tries))));
    }

    private static <T> Try<List<T>> concat(Try<List<T>> head, Try<List<T>> tail) {
        return head.flatMap(l -> tail.map(k -> concat(l, k)));
    }

    private static <T> List<T> concat(List<T> l, List<T> k) {
        ArrayList<T> retVal = new ArrayList<>(l);
        retVal.addAll(k);
        return retVal;
    }

}
