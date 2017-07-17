package com.github.vkorobkov.jfixtures.config;

import java.util.Optional;

@FunctionalInterface
public interface ConfigDigger {
    String KEY_SEPARATOR = ":";

    default <T> Optional<T> digValue(String ... sections) {
        return digValue(String.join(KEY_SEPARATOR, sections));
    }

    <T> Optional<T> digValue(String name);
}
