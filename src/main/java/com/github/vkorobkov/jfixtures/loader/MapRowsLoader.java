package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.util.StreamUtil;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MapRowsLoader {
    private final Map<String, Object> rows;

    public Collection<Row> load() {
        return rows
            .entrySet()
            .stream()
            .map(this::fixtureRow)
            .collect(Collectors.toList());
    }

    private Row fixtureRow(Map.Entry<String, ?> sourceRow) {
        Map row = Optional.ofNullable((Map)sourceRow.getValue()).orElse(Collections.emptyMap());
        return new Row(sourceRow.getKey(), loadColumns(row));
    }

    private Map<String, Value> loadColumns(Object row) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>)row;
        return data.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, this::columnValue, StreamUtil.throwingMerger(), LinkedHashMap::new)
        );
    }

    private Value columnValue(Map.Entry<String, Object> entry) {
        return new Value(entry.getValue());
    }
}
