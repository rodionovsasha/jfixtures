package com.github.vkorobkov.jfixtures.domain;

import lombok.Getter;
import lombok.val;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toMap;

public final class Table {
    @Getter
    private final String name;

    @Getter
    private final Collection<Row> rows;

    private Table(String name, Collection<Row> rows) {
        this.name = name;
        this.rows = unmodifiableCollection(rows);
    }

    public static Table ofName(String name) {
        return new Table(name, emptyList());
    }

    public static Table of(String name, Row... rows) {
        return new Table(name, Arrays.asList(rows));
    }

    public static Table of(String name, Collection<Row> rows) {
        return new Table(name, rows);
    }

    public static Table ofRow(String name, String row, Map<String, Object> columns) {
        return of(name, Row.of(row, columns));
    }

    public static Table ofRow(String name, String row, Object... keyValuePairs) {
        return of(name, Row.of(row, keyValuePairs));
    }

    public Table addRows(Row... rows) {
        return mergeRows(Arrays.asList(rows));
    }

    public Table addRows(Collection<Row> rows) {
        return mergeRows(rows);
    }

    public Table addRow(String row, Map<String, Object> columns) {
        return mergeRows(singletonList(Row.of(row, columns)));
    }

    public Table addRow(String row, Object... keyValuePairs) {
        return mergeRows(singletonList(Row.of(row, keyValuePairs)));
    }

    public static Collection<Table> mergeTables(Collection<Table> toMerge) {
        return toMerge.stream().collect(toMap(
                table -> table.name, // key
                table -> table,      // value
                (oldTable, newTable) -> oldTable.mergeRows(newTable.rows), // merge rows on conflict
                LinkedHashMap::new   // keep order
        )).values();
    }

    public Table mergeRows(Collection<Row> toMerge) {
        val mergedRows = Stream.concat(this.rows.stream(), toMerge.stream()).collect(
                toMap(Row::getName, row -> row, (oldRow, newRow) -> newRow, LinkedHashMap::new)
        ).values();
        return new Table(this.name, mergedRows);
    }
}
