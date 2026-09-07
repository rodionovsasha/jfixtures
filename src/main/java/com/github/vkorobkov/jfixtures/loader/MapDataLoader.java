package com.github.rodionovsasha.jfixtures.loader;

import com.github.rodionovsasha.jfixtures.domain.Row;
import com.github.rodionovsasha.jfixtures.domain.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
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
        return Table.of(sourceTable.getKey(), loadRows(asStringKeyedMap(sourceTable.getValue())));
    }

    private static Map<String, Object> asStringKeyedMap(Object value) {
        Map<?, ?> source = Optional.ofNullable((Map<?, ?>) value).orElse(Collections.emptyMap());
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, entry) -> result.put(String.class.cast(key), entry));
        return result;
    }

    private static Row fixtureRow(Map.Entry<String, ?> sourceRow) {
        Map<String, Object> row = asStringKeyedMap(sourceRow.getValue());
        return Row.of(sourceRow.getKey(), row);
    }
}
