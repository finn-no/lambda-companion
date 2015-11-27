package no.finntech.lambdacompanion;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a value of one of two possible types (a disjoint union.) Instances of Either are either an instance of Left or Right.
 * A common use of Either is as an alternative to Optional for dealing with possible missing values. In this usage, Optional.Absent is
 * replaced with a Left which can contain useful information. Right takes the place of Optional.Present. Convention dictates that Left is
 * used for failure and Right is used for success.
 *
 * @param <L> type of the left side
 * @param <R> type of the right side
 */
public abstract class Either<L, R> {

    /**
     * @return true if this is a Left, false otherwise.
     */
    public abstract boolean isLeft();

    /**
     * @return true if this is a Right, false otherwise.
     */
    public boolean isRight() {
        return !isLeft();
    }

    public LeftProjection<L, R> left() {
        return new LeftProjection<>(this);
    }

    public RightProjection<L, R> right() {
        return new RightProjection<>(this);
    }

    /**
     * Returns true if this and object are both either a Left or a Right, and both their
     * containing values are equal as determined by their equal methods.
     * Note that instances of differing parameterized types can be equal.
     */
    @Override
    public abstract boolean equals(final Object object);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

    /**
     * Applies leftMapper if this is a Left or rightMapper if this is a Right.
     *
     * @param leftMapper  the function to apply if this is a Left
     * @param rightMapper the function to apply if this is a Right
     * @param <X>         the resulting type of applying the function
     * @return the results of applying the function
     */
    public abstract <X> X fold(final Function<L, X> leftMapper, final Function<R, X> rightMapper);

    /**
     * Joins an Either through Left.
     * This method requires that the left side of this Either is itself an Either type. That is, this must be some type like:
     * Either[Either[X, R], R]
     * (which respects the type parameter bounds, shown below.)
     * If this instance is a Left[Either[X, R]] then the contained Either[X, R] will be returned, otherwise this value will be returned
     * unmodified.
     *
     * @param leftJoiner the function to apply if this is a Left
     * @param <X>        the resulting type of applying the function
     * @return the results of applying the function
     */
    public abstract <X> Either<X, R> joinLeft(final Function<L, Either<X, R>> leftJoiner);

    /**
     * Joins an Either through Right.
     * This method requires that the right side of this Either is itself an Either type. That is, this must be some type like:
     * Either[L, Either[L, X]]
     * (which respects the type parameter bounds, shown below.)
     * If this instance is a Right[Either[L, X]] then the contained Either[L, X] will be returned, otherwise this value will be returned
     * unmodified.
     *
     * @param rightJoiner the function to apply if this is a Right
     * @param <X>         the resulting type of applying the function
     * @return the results of applying the function
     */
    public abstract <X> Either<L, X> joinRight(final Function<R, Either<L, X>> rightJoiner);

    /**
     * @param value
     * @param <L>   the type of the left side of the resulting Left[L,R]
     * @param <R>   the type of the right side of the resulting Left[L,R]
     * @return a Left of the given value
     */
    public static <L, R> Either<L, R> left(final L value) {
        return new Left<>(value);
    }

    /**
     * @param value
     * @param <L>   the type of the left side of the resulting Right[L,R]
     * @param <R>   the type of the right side of the resulting Right[L,R]
     * @return a Right of the given value
     */
    public static <L, R> Either<L, R> right(final R value) {
        return new Right<>(value);
    }

    /**
     * Projects an Either into a Left.
     *
     * @param <L> the type of the left side of the resulting Right[L,R]
     * @param <R> the type of the right side of the resulting Right[L,R]
     */
    public static final class LeftProjection<L, R> {

        private final Either<L, R> either;

        LeftProjection(final Either<L, R> either) {
            this.either = either;
        }

        /**
         * The given function is applied if this is a Left.
         *
         * @param mapper the function to apply if this is a Left
         * @param <X>    the resulting type of applying the function
         * @return the results of applying the function
         */
        public <X> Either<X, R> map(final Function<L, X> mapper) {
            return either.joinLeft(left -> Either.left(mapper.apply(left)));
        }

        /**
         * @param predicate
         * @return Optional.Absent if this is a Right or if the given predicate p does not hold for the left value, otherwise, returns a Left.
         */
        public Optional<Either<L, R>> filter(final Predicate<L> predicate) {
            return either.fold(left -> predicate.test(left) ? Optional.of(either) : Optional.<Either<L, R>>empty(),
                               right -> Optional.<Either<L, R>>empty());
        }

        /**
         * Executes the given side-effecting function if this is a Left and returns this Either
         *
         * @param consumer
         * @return the Either within this projection
         */
        public Either<L, R> peek(final Consumer<? super L> consumer) {
            forEach(consumer);
            return either;
        }

        /**
         * Executes the given side-effecting function if this is a Left
         *
         * @param consumer
         */
        public void forEach(final Consumer<? super L> consumer) {
            toOptional().ifPresent(consumer);
        }

        /**
         * @return an Optional containing the Left value if it exists or an Optional.Absent if this is a Right.
         */
        public Optional<L> toOptional() {
            return either.fold(Optional::ofNullable, right -> Optional.<L>empty());
        }

        /**
         * @param other
         * @return the value from this Left or the given argument if this is a Right.
         */
        public L orElse(final L other) {
            return either.fold(Function.identity(), right -> other);
        }

        /**
         * @param supplier
         * @return the value from this Left or the supplied argument if this is a Right.
         */
        public L orElseGet(final Supplier<L> supplier) {
            return either.fold(Function.identity(), right -> supplier.get());
        }

        /**
         * @param <X>               Type of the exception to be thrown
         * @param exceptionMapper The function which will return the exception to be thrown
         * @return the value from this Left or throw an exception to be created by the provided function if this is a Right.
         * @throws X                    if this is a Right
         * @throws NullPointerException if no value is present and {@code exceptionSupplier} is null
         */
        public <X extends Throwable> L orElseThrow(Function<R, X> exceptionMapper) throws X {
            return either.left().toOptional().orElseThrow(() -> exceptionMapper.apply(either.right().toOptional().get()));
        }

    }

    /**
     * Projects an Either into a Right.
     *
     * @param <L> the type of the left side of the resulting Right[L,R]
     * @param <R> the type of the right side of the resulting Right[L,R]
     */
    public static final class RightProjection<L, R> {

        private final Either<L, R> either;

        RightProjection(final Either<L, R> either) {
            this.either = either;
        }

        /**
         * The given function is applied if this is a Right.
         *
         * @param mapper the function to apply if this is a Right
         * @param <X>    the resulting type of applying the function
         * @return the results of applying the function
         */
        public <X> Either<L, X> map(final Function<R, X> mapper) {
            return either.joinRight(right -> Either.right(mapper.apply(right)));
        }

        /**
         * @param predicate
         * @return Optional.Absent if this is a Left or if the given predicate p does not hold for the right value, otherwise, returns a Right.
         */
        public Optional<Either<L, R>> filter(final Predicate<R> predicate) {
            return either.fold(left -> Optional.<Either<L, R>>empty(),
                               right -> predicate.test(right) ? Optional.of(either) : Optional.<Either<L, R>>empty());
        }

        /**
         * Executes the given side-effecting function if this is a Right and returns this Either
         *
         * @param consumer
         * @return the Either within this projection
         */
        public Either<L, R> peek(final Consumer<? super R> consumer) {
            forEach(consumer);
            return either;
        }

        /**
         * Executes the given side-effecting function if this is a Right
         *
         * @param consumer
         */
        public void forEach(final Consumer<? super R> consumer) {
            toOptional().ifPresent(consumer);
        }

        /**
         * @return an Optional containing the Right value if it exists or an Optional.Absent if this is a Left.
         */
        public Optional<R> toOptional() {
            return either.fold(left -> Optional.<R>empty(), Optional::ofNullable);
        }

        /**
         * @param other
         * @return the value from this Right or the given argument if this is a Left.
         */
        public R orElse(final R other) {
            return either.fold(left -> other, Function.identity());
        }

        /**
         * @param supplier
         * @return the value from this Right or the supplied argument if this is a Left.
         */
        public R orElseGet(final Supplier<R> supplier) {
            return either.fold(left -> supplier.get(), Function.identity());
        }

        /**
         * @param <X>               Type of the exception to be thrown
         * @param exceptionMapper The function which will return the exception to be thrown
         * @return the value from this Right or throw an exception to be created by the provided function if this is a Left.
         * @throws X                    if this is a left
         * @throws NullPointerException if no value is present and {@code exceptionSupplier} is null
         */
        public <X extends Throwable> R orElseThrow(Function<L, X> exceptionMapper) throws X {
            return either.right().toOptional().orElseThrow(() -> exceptionMapper.apply(either.left().toOptional().get()));
        }
    }

    /**
     * The left side of the disjoint union, as opposed to the Right side.
     *
     * @param <L> type of the left side
     * @param <R> type of the right side
     */
    private static final class Left<L, R> extends Either<L, R> {

        private final L value;

        Left(final L value) {
            this.value = value;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public <X> X fold(final Function<L, X> leftMapper, final Function<R, X> rightMapper) {
            return leftMapper.apply(value);
        }

        @Override
        public <X> Either<X, R> joinLeft(final Function<L, Either<X, R>> leftJoiner) {
            return leftJoiner.apply(value);
        }

        @Override
        public <X> Either<L, X> joinRight(final Function<R, Either<L, X>> rightJoiner) {
            return Either.left(value);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Left left = (Left) o;
            if (value != null ? !value.equals(left.value) : left.value != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Left(" + value + ")";
        }

    }

    /**
     * The right side of the disjoint union, as opposed to the Left side.
     *
     * @param <L> type of the left side
     * @param <R> type of the right side
     */
    private static final class Right<L, R> extends Either<L, R> {

        private final R value;

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public <X> X fold(final Function<L, X> leftMapper, final Function<R, X> rightMapper) {
            return rightMapper.apply(value);
        }

        @Override
        public <X> Either<X, R> joinLeft(final Function<L, Either<X, R>> leftJoiner) {
            return Either.right(value);
        }

        @Override
        public <X> Either<L, X> joinRight(final Function<R, Either<L, X>> rightJoiner) {
            return rightJoiner.apply(value);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Right right = (Right) o;
            if (value != null ? !value.equals(right.value) : right.value != null) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Right(" + value + ")";
        }

        Right(final R value) {
            this.value = value;
        }

    }

}
