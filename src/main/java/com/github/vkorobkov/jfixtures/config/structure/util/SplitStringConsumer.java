package com.github.vkorobkov.jfixtures.config.structure.util;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.function.Consumer;


@AllArgsConstructor
public class SplitStringConsumer implements Consumer {

    private final Consumer<String> delegate;

    @Override
    public void accept(Object value) {
        Arrays
            .asList((String.valueOf(value)).split(","))
            .stream()
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .forEach(delegate);
    }
}
