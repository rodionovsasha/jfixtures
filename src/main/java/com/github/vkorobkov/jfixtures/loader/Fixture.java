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
    private final Collection<Row> rows;

    public Fixture(String name, Collection<Row> rows) {
        this.name = name;
        this.rows = Collections.unmodifiableCollection(rows);
    }

    public static Collection<Fixture> mergeFixtures(Collection<Fixture> toMerge) {
        return toMerge.stream().collect(Collectors.toMap(
            fixture -> fixture.name, // key
            fixture -> fixture, // value
            (oldFixture, newFixture) -> oldFixture.mergeRows(newFixture.rows), // merge rows on conflict
            LinkedHashMap::new // keep order
        )).values();
    }

    public Fixture mergeRows(Collection<Row> toMerge) {
        val mergedRows = Stream.concat(this.rows.stream(), toMerge.stream()).collect(
            Collectors.toMap(Row::getName, row -> row, (oldRow, newRow) -> newRow, LinkedHashMap::new)
        ).values();
        return new Fixture(this.name, mergedRows);
    }
}
