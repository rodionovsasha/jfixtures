package io.github.rodionovsasha.jfixtures.sql;

import io.github.rodionovsasha.jfixtures.config.structure.tables.CleanMethod;
import io.github.rodionovsasha.jfixtures.domain.Value;
import io.github.rodionovsasha.jfixtures.domain.ValueType;
import io.github.rodionovsasha.jfixtures.instructions.CleanTable;
import io.github.rodionovsasha.jfixtures.instructions.CustomSql;
import io.github.rodionovsasha.jfixtures.instructions.InsertRow;
import io.github.rodionovsasha.jfixtures.util.SqlUtil;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.stream.Collectors;

public interface SqlBase extends Sql {
    @SneakyThrows
    @Override
    default void cleanTable(Appender appender, CleanTable cleanTable) {
        var table = escapeTableOrColumn(cleanTable.getTable());
        var cleanMethod = cleanTable.getCleanMethod();

        if (cleanMethod == CleanMethod.DELETE) {
            appender.append("DELETE FROM ", table, ";\n");
        } else if (cleanMethod == CleanMethod.TRUNCATE) {
            appender.append("TRUNCATE TABLE ", table, ";\n");
        } else if (cleanMethod == CleanMethod.TRUNCATE_CASCADE) {
            appender.append("TRUNCATE TABLE ", table, " CASCADE;\n");
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

    default String escapeValue(Value value) {
        String str = value.getSqlRepresentation();
        return value.getType() == ValueType.TEXT ? SqlUtil.escapeString(str) : str;
    }

    String escapeTableOrColumnPart(String part);
}
