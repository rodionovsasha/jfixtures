package com.github.vkorobkov.jfixtures.util;

import java.util.function.BinaryOperator;

public final class StreamUtil {
    private StreamUtil() {
    }

    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
