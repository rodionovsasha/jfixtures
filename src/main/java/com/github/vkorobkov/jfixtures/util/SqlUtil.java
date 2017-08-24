package com.github.vkorobkov.jfixtures.util;

import java.util.Objects;

public final class SqlUtil {
    private static final String SINGLE_QUOTE = "'";
    private static final String SINGLE_QUOTE_ESCAPED = "''";

    private SqlUtil() {
    }

    public static String escapeString(String string) {
        string = string.replace(SINGLE_QUOTE, SINGLE_QUOTE_ESCAPED);
        return SqlUtil.surround(string, SINGLE_QUOTE);
    }

    public static String surround(String string, String with) {
        return surround(string, with, with);
    }

    public static String surround(String string, String startsWith, String endsWith) {
        startsWith = Objects.requireNonNull(startsWith, "Can not surround string with null start prefix");
        endsWith = Objects.requireNonNull(endsWith, "Can not surround string with null end suffix");
        return startsWith
                + Objects.requireNonNull(string, "The input string can not be null")
                + endsWith;
    }
}
