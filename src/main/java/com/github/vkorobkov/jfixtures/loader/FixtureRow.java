package com.github.vkorobkov.jfixtures.loader;

import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.Map;

@EqualsAndHashCode
public final class FixtureRow {
    public final String name;
    public final Map<String, FixtureValue> columns;

    public FixtureRow(String name, Map<String, FixtureValue> columns) {
        this.name = name;
        this.columns = Collections.unmodifiableMap(columns);
    }
}
