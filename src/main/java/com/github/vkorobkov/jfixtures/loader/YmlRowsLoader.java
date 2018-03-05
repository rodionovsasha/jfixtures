package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.util.StreamUtil;
import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public class YmlRowsLoader implements Supplier<Collection<FixtureRow>> {
    private final Path file;

    @Override
    public Collection<FixtureRow> get() {
        return YmlUtil.load(file)
            .entrySet()
            .stream()
            .map(this::fixtureRow)
            .collect(Collectors.toList());
    }

    private FixtureRow fixtureRow(Map.Entry<String, ?> sourceRow) {
        Map row = Optional.ofNullable((Map)sourceRow.getValue()).orElse(Collections.emptyMap());
        return new FixtureRow(sourceRow.getKey(), loadColumns(row));
    }

    private Map<String, FixtureValue> loadColumns(Object row) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>)row;
        return data.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, this::columnValue, StreamUtil.throwingMerger(), LinkedHashMap::new)
        );
    }

    private FixtureValue columnValue(Map.Entry<String, Object> entry) {
        return new FixtureValue(entry.getValue());
    }
}
