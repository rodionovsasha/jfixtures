package io.github.rodionovsasha.jfixtures.loader;

import io.github.rodionovsasha.jfixtures.domain.Row;
import io.github.rodionovsasha.jfixtures.domain.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapDataLoader {

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
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static Table fixtureTable(Map.Entry<String, ?> sourceTable) {
        return Table.of(sourceTable.getKey(), loadRows(tableRows(sourceTable.getKey(), sourceTable.getValue())));
    }

    private static Map<String, Object> asStringKeyedMap(Object value) {
        if (value == null) {
            return Collections.emptyMap();
        }
        if (!(value instanceof Map<?, ?> source)) {
            throw new LoaderException("Fixture row must be a map, but was [" + value.getClass() + "]");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, entry) -> result.put(String.class.cast(key), entry));
        return result;
    }

    private static Map<String, Object> tableRows(String table, Object value) {
        if (value == null || value instanceof Map<?, ?>) {
            return asStringKeyedMap(value);
        }
        if (value instanceof List<?> rows && rows.isEmpty()) {
            return Collections.emptyMap();
        }
        String type = value.getClass().getName();
        throw new LoaderException("Fixture table [" + table
                + "] must be a map or an empty list, but was [" + type + "]");
    }

    private static Row fixtureRow(Map.Entry<String, ?> sourceRow) {
        if (sourceRow.getKey().startsWith(".")) {
            return null;
        }
        Map<String, Object> row = asStringKeyedMap(sourceRow.getValue());
        return Row.of(sourceRow.getKey(), row);
    }
}
