package com.github.vkorobkov.jfixtures.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.function.BiFunction;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RowMergeConflictResolver implements BiFunction<Object, Object, Object> {

    public static final String COLUMN_VALUE_KEY = "value";
    public static final RowMergeConflictResolver INSTANCE = new RowMergeConflictResolver();

    @Override
    public Object apply(Object from, Object with) {
        if (with instanceof Map) {
            ((Map)with).putIfAbsent(COLUMN_VALUE_KEY, from);
            return with;
        }
        ((Map)from).put(COLUMN_VALUE_KEY, with);
        return from;
    }
}
