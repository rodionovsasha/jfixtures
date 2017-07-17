package com.github.vkorobkov.jfixtures.loader;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public final class FixtureValue {
    private final Object value;
    private final ValueType type;

    private FixtureValue(Object value, ValueType type) {
        this.value = value;
        this.type = type;
    }

    public static FixtureValue ofAuto(Object value) {
        return new FixtureValue(value, ValueType.AUTO);
    }

    public static FixtureValue ofSql(String sql) {
        return new FixtureValue(sql, ValueType.SQL);
    }

    public boolean isString() {
        return isStandardType(String.class);
    }

    public boolean isSql() {
        return type == ValueType.SQL;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    private boolean isStandardType(Class<?> typeToCheck) {
        return type == ValueType.AUTO && typeToCheck.isAssignableFrom(value.getClass());
    }
}
