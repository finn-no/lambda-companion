package no.finntech.lambdacompanion;

import java.util.List;
import java.util.function.BiFunction;

public final class Functions {

    private Functions() {
    }

    /**
     * @param <A> a
     * @param <B> b
     * @param accumulator accumulator
     * @param b b
     * @param list list
     * @return b
     * Head recursive fold. Fold the list by combining the first element with the results of combining the rest
     * <p>
     * Example:
     * <pre>
     * {@code
     *    :                                    f
     *   / \       foldRight f z ->           / \
     *  1   :                                1   f
     *     / \                                  / \
     *    2  []                                2   z
     * }
     * </pre>
     */
    public static <A, B> B foldRight(final BiFunction<A, B, B> accumulator, final B b, final List<A> list) {
        return list.isEmpty() ? b : accumulator.apply(head(list), foldRight(accumulator, b, tail(list)));
    }

    /**
     * @param <A> a
     * @param <B> b
     * @param accumulator accumulator
     * @param b b
     * @param list list
     * @return b
     * Tail recursive fold. Fold the list by recursively combining the results of combining all but the last element with the last one
     * <p>
     * Example:
     * <pre>
     * {@code
     *    :                                    f
     *   / \       foldLeft f z ->            / \
     *  1   :                                f   2
     *     / \                              / \
     *    2  []                            z   1
     * }
     * </pre>
     */
    public static <A, B> B foldLeft(final BiFunction<A, B, B> accumulator, final B b, final List<A> list) {
        return list.isEmpty() ? b : foldLeft(accumulator, accumulator.apply(head(list), b), tail(list));
    }

    /**
     * @param <A> a
     * @param list list
     * @return first element of the given list.
     * @throws IndexOutOfBoundsException if the list is empty
     */
    public static <A> A head(final List<A> list) {
        return list.get(0);
    }

    /**
     * @param <A> a
     * @param list list
     * @return all but the first elements of the given list
     * @throws IndexOutOfBoundsException if the list is empty
     */
    public static <A> List<A> tail(final List<A> list) {
        return list.subList(1, list.size());
    }

}
