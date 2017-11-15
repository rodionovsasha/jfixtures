package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod;
import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.CustomSql;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.loader.FixtureValue;
import com.github.vkorobkov.jfixtures.loader.ValueType;
import com.github.vkorobkov.jfixtures.util.SqlUtil;
import lombok.SneakyThrows;
import lombok.val;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface SqlBase extends Sql {
    @SneakyThrows
    @Override
    default void cleanTable(Appender appender, CleanTable cleanTable) {
        val table = escapeTableOrColumn(cleanTable.getTable());
        val cleanMethod = cleanTable.getCleanMethod();

        if (cleanMethod == CleanMethod.DELETE) {
            appender.append("DELETE FROM ", table, ";\n");
        } else if (cleanMethod == CleanMethod.TRUNCATE) {
            appender.append("TRUNCATE TABLE ", table, ";\n");
        }
    }

    @SneakyThrows
    @Override
    default void insertRow(Appender appender, InsertRow insertRow) {
        String table = escapeTableOrColumn(insertRow.getTable());

        String columns = insertRow.getValues().keySet().stream()
            .map(this::escapeTableOrColumnPart)
            .collect(Collectors.joining(", "));

        String values = insertRow.getValues().values().stream()
            .map(this::escapeValue)
            .collect(Collectors.joining(", "));

        appender.append("INSERT INTO ", table, " (", columns, ") VALUES (", values, ");\n");
    }

    @SneakyThrows
    @Override
    default void addCustomSql(Appender appender, CustomSql customSql) {
        appender.append(customSql.getInstruction(), "\n");
    }

    default String escapeTableOrColumn(String name) {
        return Arrays
            .stream(name.split("\\."))
            .map(this::escapeTableOrColumnPart)
            .collect(Collectors.joining("."));
    }

    default String escapeValue(FixtureValue value) {
        String str = value.getSqlRepresentation();
        return value.getType() == ValueType.TEXT ? SqlUtil.escapeString(str) : str;
    }

    String escapeTableOrColumnPart(String part);
}
