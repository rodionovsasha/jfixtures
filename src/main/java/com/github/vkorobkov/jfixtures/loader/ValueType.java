package com.github.vkorobkov.jfixtures.loader;

public enum ValueType {
    AUTO,
    SQL;

    public static ValueType valueOfIgnoreCase(String type) {
        return ValueType.valueOf(type.toUpperCase());
    }
}
