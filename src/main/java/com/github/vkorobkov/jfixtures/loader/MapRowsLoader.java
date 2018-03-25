package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.domain.Row;
import lombok.AllArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
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
        return new Row(sourceRow.getKey(), row);
    }
}
