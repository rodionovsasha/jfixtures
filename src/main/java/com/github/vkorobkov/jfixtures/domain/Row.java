package com.github.vkorobkov.jfixtures.domain;

import com.github.vkorobkov.jfixtures.util.CollectionUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@EqualsAndHashCode
@Getter
public final class Row {
    private final String name;
    private final Map<String, Value> columns;

    public Row(String name, Map<String, ?> columns) {
        this.name = name;
        this.columns = Collections.unmodifiableMap(
            CollectionUtil.mapValues(columns, Value::of)
        );
    }

    public Row columns(Map<String, ?> toMerge) {
        val merged = CollectionUtil.merge(
            columns,
            CollectionUtil.mapValues(toMerge, Value::of)
        );
        return new Row(name, merged);
    }

    public Row columns(Object... keyValuePairs) {
        int pairsLength = keyValuePairs.length;

        if (pairsLength % 2 != 0) {
            throw new IllegalArgumentException("Odd number of key/value pairs");
        }

        Map<String, Object> keyValueMap = new LinkedHashMap<>();

        for (int i = 0; i < pairsLength; i += 2) {
            if (!(keyValuePairs[i] instanceof String)) {
                throw new IllegalArgumentException("Key must be a string");
            }

            keyValueMap.put((String) keyValuePairs[i], keyValuePairs[i + 1]);
        }

        return columns(keyValueMap);
    }
}
