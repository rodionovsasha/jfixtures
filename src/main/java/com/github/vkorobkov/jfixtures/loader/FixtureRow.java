package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.util.CollectionUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;

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

    public FixtureRow withBaseColumns(Map<String, FixtureValue> base) {
        if (base.isEmpty()) {
            return this;
        }
        val mergedColumns = CollectionUtil.merge(base, columns);
        return new FixtureRow(name, mergedColumns);
    }
}
