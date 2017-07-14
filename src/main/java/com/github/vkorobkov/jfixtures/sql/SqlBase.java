package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.loader.FixtureValue;
import com.github.vkorobkov.jfixtures.util.SqlUtil;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface SqlBase extends Sql {
    @SneakyThrows
    @Override
    default void cleanTable(Appender appender, CleanTable cleanTable) {
        String table = escapeTableOrColumn(cleanTable.table);
        appender.append("DELETE FROM ", table, ";\n");
    }

    @SneakyThrows
    @Override
    default void insertRow(Appender appender, InsertRow insertRow) {
        String table = escapeTableOrColumn(insertRow.table);

        String columns = insertRow.values.keySet().stream()
            .map(this::escapeTableOrColumnPart)
            .collect(Collectors.joining(", "));

        String values = insertRow.values.values().stream()
            .map(this::escapeValue)
            .collect(Collectors.joining(", "));

        appender.append("INSERT INTO ", table, " (", columns, ") VALUES (", values, ");\n");
    }

    default String escapeTableOrColumn(String name) {
        return Arrays
            .stream(name.split("\\."))
            .map(this::escapeTableOrColumnPart)
            .collect(Collectors.joining("."));
    }

    default String escapeValue(FixtureValue value) {
        String str = value.toString();
        return value.isString() ? SqlUtil.escapeString(str) : str;
    }

    String escapeTableOrColumnPart(String part);
}