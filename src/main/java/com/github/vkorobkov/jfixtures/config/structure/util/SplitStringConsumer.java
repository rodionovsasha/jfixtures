package com.github.rodionovsasha.jfixtures.config.structure.util;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.function.Consumer;


@AllArgsConstructor
public class SplitStringConsumer implements Consumer<Object> {

    private final Consumer<String> delegate;

    @Override
    public void accept(Object value) {
        Arrays.stream((String.valueOf(value)).split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(delegate);
    }
}
