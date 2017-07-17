package com.github.vkorobkov.jfixtures.loader;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@EqualsAndHashCode
@Getter
public final class FixtureRow {
    private final String name;
    private final Map<String, FixtureValue> columns;

    public FixtureRow(String name, Map<String, FixtureValue> columns) {
        this.name = name;
        this.columns = Collections.unmodifiableMap(columns);
    }
}
