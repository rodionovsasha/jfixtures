package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.util.MapMerger;
import com.github.vkorobkov.jfixtures.util.StreamUtil;
import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public class YmlRowsLoader implements Supplier<Collection<FixtureRow>> {
    private final Path file;
    private final Map<String, Object> base;

    @Override
    public Collection<FixtureRow> get() {
        return loadYamlContent(file)
                .entrySet()
                .stream()
                .map(this::fixtureRow)
                .collect(Collectors.toList());
    }

    private FixtureRow fixtureRow(Map.Entry<String, ?> sourceRow) {
        Map row = Optional.ofNullable((Map) sourceRow.getValue()).orElse(Collections.emptyMap());
        Map merged = MapMerger.merge(base, row);
        return new FixtureRow(sourceRow.getKey(), loadColumns(merged));
    }

    private Map<String, FixtureValue> loadColumns(Object row) {
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) row;
        return data.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, this::columnValue, StreamUtil.throwingMerger(), LinkedHashMap::new)
        );
    }

    private FixtureValue columnValue(Map.Entry<String, Object> entry) {
        return new FixtureValue(entry.getValue());
    }

    private Map<String, Object> loadYamlContent(Path file) {
        try {
            return YmlUtil.load(file);
        } catch (IOException cause) {
            String message = "Can not load fixture file: " + file;
            throw new LoaderException(message, cause);
        }
    }
}
