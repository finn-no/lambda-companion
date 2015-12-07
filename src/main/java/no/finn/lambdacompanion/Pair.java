package no.finn.lambdacompanion;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents two values of two possible types together (a union of 2.)
 *
 * @param <L> type of the left side
 * @param <R> type of the right side
 */
public final class Pair<L, R> {

    private final L left;

    private final R right;

    public Pair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    public Map.Entry<L, R> toMapEntry() {
        return new AbstractMap.SimpleEntry<>(left, right);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Pair pair = (Pair) o;

        if (left != null ? !left.equals(pair.left) : pair.left != null) {
            return false;
        }
        if (right != null ? !right.equals(pair.right) : pair.right != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    /**
     * Given a list of Either[L,R] that can be a mixed collection of Left and Right, groups left side values into one list and
     * right side values into another list and return both lists together as a Pair (makes a union of the values contained within a
     * collection of disjoint unions)
     *
     * @param collection a collection of disjoint unions
     * @param <L>        type of the left side
     * @param <R>        type of the right side
     * @return a Pair of the left values and right values
     */
    public static <L, R> Pair<List<L>, List<R>> of(final List<Either<L, R>> collection) {
        return new Pair<List<L>, List<R>>(collection.stream()
                                                    .flatMap(cat -> cat.left().map(Stream::<L>of).left().orElseGet(Stream::empty))
                                                    .collect(Collectors.toList()),
                                          collection.stream()
                                                    .flatMap(cat -> cat.right().map(Stream::<R>of).right().orElseGet(Stream::empty))
                                                    .collect(Collectors.toList()));
    }

    /**
     * Shorthand to collect a {@link java.util.stream.Stream} of Pair-s to a {@link java.util.Map} using left value as the key and right
     * value as the value of the {@link java.util.Map} entries
     * <p>
     * Warning: this Collector will only work when the Stream does not contain duplicate left values in the Pair-s it contains.
     *
     * @param <K> k
     * @param <U> u
     * @return a {@link java.util.stream.Collector}
     */
    public static <K, U> Collector<Pair<K, U>, ?, Map<K, U>> toMap() {
        return Collectors.toMap(Pair::getLeft, Pair::getRight);
    }

}
