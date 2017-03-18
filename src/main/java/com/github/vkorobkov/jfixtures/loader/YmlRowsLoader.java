package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.util.StreamUtil;
import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.AllArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
public class YmlRowsLoader implements Supplier<Collection<FixtureRow>> {
    private final Path file;

    @Override
    public Collection<FixtureRow> get() {
        return loadYamlContent(file)
            .entrySet()
            .stream()
            .map(this::fixtureRow)
            .collect(Collectors.toList());
    }

    private FixtureRow fixtureRow(Map.Entry<String, ?> sourceRow) {
        String name = sourceRow.getKey();
        Map<String, FixtureValue> columns = loadColumns(sourceRow.getValue());
        return new FixtureRow(name, columns);
    }

    private Map<String, FixtureValue> loadColumns(Object row) {
        if (row == null) {
            return Collections.emptyMap();
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>)row;
        val collector = Collectors.toMap(Map.Entry::getKey, this::columnValue, StreamUtil.throwingMerger(),
                LinkedHashMap::new);
        return data.entrySet().stream().collect(collector);
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
