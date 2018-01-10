package com.github.vkorobkov.jfixtures.util;


import java.util.function.Consumer;

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
}
