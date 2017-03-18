package com.github.vkorobkov.jfixtures.sql;

import java.io.IOException;

@FunctionalInterface
public interface Appender {
    default void append(CharSequence ... sequences) throws IOException {
        for (CharSequence sequence : sequences) {
            append(sequence);
        }
    }

    void append(CharSequence sequence) throws IOException;
}
