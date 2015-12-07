package no.finn.lambdacompanion;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Streams {

    public static <T> ExtendedStream<T> stream(final Collection<T> collection) {
        return stream(collection.stream());
    }

    public static <T> ExtendedStream<T> stream(final Stream<T> stream) {
        return ExtendedStream.of(stream);
    }

    public static <T extends Map.Entry<K, U>, K, U> Collector<T, ?, Map<K, U>> entryToMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

}
