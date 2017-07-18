package com.github.vkorobkov.jfixtures.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;


public final class MapMerger {
    private MapMerger() {
    }

    public static Map merge(Map from, Map with, BiFunction<Object, Object, Object> resolver) {
        Map into = new LinkedHashMap(from);
        with.forEach((name, withNode) -> {
            Object intoNode = into.get(name);
            if (intoNode == null && withNode instanceof Map) {
                intoNode = Collections.emptyMap();
            }
            into.put(name, intoNode == null ? withNode : replacement(intoNode, withNode, resolver));
        });
        return into;
    }

    private static Object replacement(Object intoNode, Object withNode, BiFunction<Object, Object, Object> resolver) {
        boolean intoIsNode = intoNode instanceof Map;
        boolean withIsNode = withNode instanceof Map;

        if (!intoIsNode && !withIsNode) {
            return withNode;
        }
        if (intoIsNode && withIsNode) {
            return merge((Map) intoNode, (Map) withNode, resolver);
        }
        return resolver.apply(cloneIfMap(intoNode), cloneIfMap(withNode));
    }

    private static Object cloneIfMap(Object object) {
        return object instanceof Map ? new LinkedHashMap((Map)object) : object;
    }
}
