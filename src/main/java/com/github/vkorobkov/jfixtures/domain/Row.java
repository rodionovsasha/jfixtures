package com.github.vkorobkov.jfixtures.domain;

import com.github.vkorobkov.jfixtures.util.CollectionUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

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
            throw new IllegalArgumentException(
                    "Parameter <keyValuePairs> is expected to have odd length since it represents key/value pairs"
            );
        }

        Map<String, Object> keyValueMap = new LinkedHashMap<>(pairsLength / 2);

        for (int i = 0; i < pairsLength; i += 2) {
            keyValueMap.put(castColumnName(keyValuePairs[i]), keyValuePairs[i + 1]);
        }

        return columns(keyValueMap);
    }

    public Row column(String name, Object value) {
        return columns(singletonMap(name, value));
    }

    public Row nullColumn(String name) {
        return columns(singletonMap(name, Value.ofNull()));
    }

    public Row sqlColumn(String name, String sql) {
        return columns(singletonMap(name, Value.ofSql(sql)));
    }

    public Row textColumn(String name, String text) {
        return columns(singletonMap(name, Value.ofText(text)));
    }

    private String castColumnName(Object name) {
        if (name instanceof String) {
            return (String) name;
        }
        String actualValue = name == null ? null : "class = [" + name.getClass() + "], value = [" + name + "]";
        String message = "Column name is expected to be a string, but was passed " + actualValue;
        throw new IllegalArgumentException(message);
    }
}
