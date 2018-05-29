package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.domain.Row;
import com.github.vkorobkov.jfixtures.domain.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MapDataLoader {
    private MapDataLoader() {
    }

    public static Collection<Table> loadTables(Map<String, ?> tables) {
        if (tables == null) {
            return Collections.emptyList();
        }

        return tables
                .entrySet()
                .stream()
                .map(MapDataLoader::fixtureTable)
                .collect(Collectors.toList());
    }

    public static Collection<Row> loadRows(Map<String, Object> rows) {
        if (rows == null) {
            return Collections.emptyList();
        }

        return rows
                .entrySet()
                .stream()
                .map(MapDataLoader::fixtureRow)
                .collect(Collectors.toList());
    }

    private static Table fixtureTable(Map.Entry<String, ?> sourceTable) {
        return Table.of(sourceTable.getKey(), loadRows((Map<String, Object>) sourceTable.getValue()));
    }

    private static Row fixtureRow(Map.Entry<String, ?> sourceRow) {
        Map row = Optional.ofNullable((Map) sourceRow.getValue()).orElse(Collections.emptyMap());
        return Row.of(sourceRow.getKey(), row);
    }
}
