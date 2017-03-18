package com.github.vkorobkov.jfixtures.config;

import java.util.Optional;

public class EmptyDigger implements ConfigDigger {
    @Override
    public <T> Optional<T> digValue(String name) {
        return Optional.empty();
    }
}
