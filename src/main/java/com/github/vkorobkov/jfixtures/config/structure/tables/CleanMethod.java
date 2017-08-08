package com.github.vkorobkov.jfixtures.config.structure.tables;

public enum CleanMethod {
    DELETE,
    NONE;

    public static CleanMethod valueOfIgnoreCase(String type) {
        return CleanMethod.valueOf(type.toUpperCase());
    }
}
