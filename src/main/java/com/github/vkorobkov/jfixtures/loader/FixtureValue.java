package com.github.vkorobkov.jfixtures.loader;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode
public final class FixtureValue {
    public final Object value;

    public boolean isString() {
        return value instanceof String;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
