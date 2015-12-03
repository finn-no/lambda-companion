package no.finntech.lambdacompanion;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Wrapper around an {@link java.util.stream.Stream} that provides some missed functions like
 * {@link #foldRight(java.util.function.BiFunction, Object)}, {@link #toList()} and {@link #toSet()}
 */
public class ExtendedStream<T> implements Stream<T> {

    private final Stream<T> delegate;

    protected ExtendedStream(final Stream<T> delegate) {
        this.delegate = delegate;
    }

    public <R> R foldRight(final BiFunction<T, R, R> accumulator, final R identity) {
        return Functions.foldRight(accumulator, identity, this.toList());
    }

    public StreamableOptional<T> findLast() {
        return StreamableOptional.ofOptional(this.reduce((a, b) -> b));
    }

    public List<T> toList() {
        return collect(Collectors.toList());
    }

    public Set<T> toSet() {
        return collect(Collectors.toSet());
    }

    // ---- Delegation of inherited methods

    @Override
    public <R> ExtendedStream<R> map(final Function<? super T, ? extends R> mapper) {
        return of(delegate.<R>map(mapper));
    }

    @Override
    public <R> ExtendedStream<R> flatMap(final Function<? super T, ? extends Stream<? extends R>> mapper) {
        return of(delegate.<R>flatMap(mapper));
    }

    @Override
    public ExtendedStream<T> filter(final Predicate<? super T> predicate) {
        return of(delegate.filter(predicate));
    }

    @Override
    public ExtendedStream<T> distinct() {
        return of(delegate.distinct());
    }

    @Override
    public ExtendedStream<T> sorted() {
        return of(delegate.sorted());
    }

    @Override
    public ExtendedStream<T> sorted(final Comparator<? super T> comparator) {
        return of(delegate.sorted(comparator));
    }

    @Override
    public ExtendedStream<T> peek(final Consumer<? super T> action) {
        return of(delegate.peek(action));
    }

    @Override
    public ExtendedStream<T> limit(final long maxSize) {
        return of(delegate.limit(maxSize));
    }

    @Override
    public ExtendedStream<T> skip(final long n) {
        return of(delegate.skip(n));
    }

    @Override
    public ExtendedStream<T> sequential() {
        return of(delegate.sequential());
    }

    @Override
    public ExtendedStream<T> parallel() {
        return of(delegate.parallel());
    }

    @Override
    public ExtendedStream<T> unordered() {
        return of(delegate.unordered());
    }

    @Override
    public ExtendedStream<T> onClose(final Runnable closeHandler) {
        return of(delegate.onClose(closeHandler));
    }

    @Override
    public IntStream mapToInt(final ToIntFunction<? super T> mapper) {
        return delegate.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(final ToLongFunction<? super T> mapper) {
        return delegate.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(final ToDoubleFunction<? super T> mapper) {
        return delegate.mapToDouble(mapper);
    }

    @Override
    public IntStream flatMapToInt(final Function<? super T, ? extends IntStream> mapper) {
        return delegate.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(final Function<? super T, ? extends LongStream> mapper) {
        return delegate.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(final Function<? super T, ? extends DoubleStream> mapper) {
        return delegate.flatMapToDouble(mapper);
    }

    @Override
    public void forEach(final Consumer<? super T> action) {
        delegate.forEach(action);
    }

    @Override
    public void forEachOrdered(final Consumer<? super T> action) {
        delegate.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <A> A[] toArray(final IntFunction<A[]> generator) {
        return delegate.toArray(generator);
    }

    @Override
    public T reduce(final T identity, final BinaryOperator<T> accumulator) {
        return delegate.reduce(identity, accumulator);
    }

    @Override
    public Optional<T> reduce(final BinaryOperator<T> accumulator) {
        return delegate.reduce(accumulator);
    }

    @Override
    public <U> U reduce(final U identity, final BiFunction<U, ? super T, U> accumulator, final BinaryOperator<U> combiner) {
        return delegate.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(final Supplier<R> supplier, final BiConsumer<R, ? super T> accumulator, final BiConsumer<R, R> combiner) {
        return delegate.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(final Collector<? super T, A, R> collector) {
        return delegate.collect(collector);
    }

    @Override
    public Optional<T> min(final Comparator<? super T> comparator) {
        return delegate.min(comparator);
    }

    @Override
    public Optional<T> max(final Comparator<? super T> comparator) {
        return delegate.max(comparator);
    }

    @Override
    public long count() {
        return delegate.count();
    }

    @Override
    public boolean anyMatch(final Predicate<? super T> predicate) {
        return delegate.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(final Predicate<? super T> predicate) {
        return delegate.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(final Predicate<? super T> predicate) {
        return delegate.noneMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() {
        return delegate.findFirst();
    }

    @Override
    public Optional<T> findAny() {
        return delegate.findAny();
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
        return delegate.isParallel();
    }

    @Override
    public void close() {
        delegate.close();
    }

    public static <T> ExtendedStream<T> of(final Stream<T> stream) {
        return new ExtendedStream<>(stream);
    }
}
