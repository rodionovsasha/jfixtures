package com.github.vkorobkov.jfixtures.util;

import java.util.LinkedHashMap;
import java.util.Map;


public final class MapMerger {
    private MapMerger() {
    }

    public static Map merge(Map from, Map with) {
        Map into = new LinkedHashMap(from);
        with.forEach((name, withNode) -> {
            Object intoNode = into.get(name);
            if (withNode instanceof Map && intoNode instanceof Map) {
                withNode = merge((Map) intoNode, (Map) withNode);
            }
            into.put(name, cloneIfMap(withNode));
        });
        return into;
    }

    private static Object cloneIfMap(Object object) {
        return object instanceof Map ? new LinkedHashMap((Map)object) : object;
    }
}
