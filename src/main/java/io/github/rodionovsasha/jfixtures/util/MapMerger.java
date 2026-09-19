package io.github.rodionovsasha.jfixtures.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapMerger {

    public static <K> Map<K, Object> merge(Map<? extends K, ?> from, Map<? extends K, ?> with) {
        Map<K, Object> into = new LinkedHashMap<>(from);
        with.forEach((name, withNode) -> {
            Object intoNode = into.get(name);
            Object mergedNode = withNode;
            if (withNode instanceof Map && intoNode instanceof Map) {
                mergedNode = merge((Map<?, ?>) intoNode, (Map<?, ?>) withNode);
            }
            into.put(name, cloneIfMap(mergedNode));
        });
        return into;
    }

    private static Object cloneIfMap(Object object) {
        return object instanceof Map ? new LinkedHashMap<>((Map<?, ?>)object) : object;
    }
}
