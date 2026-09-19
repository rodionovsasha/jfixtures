package io.github.rodionovsasha.jfixtures.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.BinaryOperator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtil {

    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
