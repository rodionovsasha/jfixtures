package com.github.vkorobkov.jfixtures.domain;

import lombok.Getter;
import lombok.val;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Table {
    public final String name;

    @Getter
    private final Collection<Row> rows;

    public Table(String name, Collection<Row> rows) {
        this.name = name;
        this.rows = Collections.unmodifiableCollection(rows);
    }

    public static Collection<Table> mergeTables(Collection<Table> toMerge) {
        return toMerge.stream().collect(Collectors.toMap(
            table -> table.name, // key
            table -> table, // value
            (oldTable, newTable) -> oldTable.mergeRows(newTable.rows), // merge rows on conflict
            LinkedHashMap::new // keep order
        )).values();
    }

    public Table mergeRows(Collection<Row> toMerge) {
        val mergedRows = Stream.concat(this.rows.stream(), toMerge.stream()).collect(
            Collectors.toMap(Row::getName, row -> row, (oldRow, newRow) -> newRow, LinkedHashMap::new)
        ).values();
        return new Table(this.name, mergedRows);
    }
}
