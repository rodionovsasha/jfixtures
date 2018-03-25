package com.github.vkorobkov.jfixtures.util;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtil {

    private CollectionUtil() {
    }

    public static void flattenRecursively(Object toFlat, Consumer consumer) {
        if (isIterable(toFlat)) {
            ((Iterable<Object>)toFlat).forEach(item -> flattenRecursively(item, consumer));
        } else {
            consumer.accept(toFlat);
        }
    }

    private static boolean isIterable(Object object) {
        return object instanceof Iterable;
    }

    public static <K, VFrom, VTo> Map<K, VTo> mapValues(Map<K, VFrom> map, Function<VFrom, VTo> converter) {
        return map.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> converter.apply(entry.getValue()),
            StreamUtil.throwingMerger(),
            LinkedHashMap::new // preserve elements order
        ));
    }

    public static <K, V> Map<K, V> merge(Map<K, V> into, Map<K, V> with) {
        Map<K, V> result = new HashMap<>(into.size() + with.size());
        result.putAll(into);
        result.putAll(with);
        return result;
    }
}
