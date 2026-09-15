package com.github.rodionovsasha.jfixtures.config.structure.tables;

public enum CleanMethod {
    DELETE,
    TRUNCATE,
    TRUNCATE_CASCADE,
    NONE;

    public static CleanMethod valueOfIgnoreCase(String type) {
        return CleanMethod.valueOf(type.toUpperCase());
    }
}
