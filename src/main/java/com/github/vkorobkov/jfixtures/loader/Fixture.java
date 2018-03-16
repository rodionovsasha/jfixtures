package com.github.vkorobkov.jfixtures.loader;

import lombok.Getter;
import lombok.val;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Fixture {
    public final String name;

    @Getter
    private final Collection<FixtureRow> rows;

    public Fixture(String name, Collection<FixtureRow> rows) {
        this.name = name;
        this.rows = Collections.unmodifiableCollection(rows);
    }

    public Fixture mergeRows(Collection<FixtureRow> toMerge) {
        val mergedRows = Stream.concat(this.rows.stream(), toMerge.stream()).collect(
            Collectors.toMap(FixtureRow::getName, row -> row, (oldRow, newRow) -> newRow, LinkedHashMap::new)
        ).values();
        return new Fixture(this.name, mergedRows);
    }
}
