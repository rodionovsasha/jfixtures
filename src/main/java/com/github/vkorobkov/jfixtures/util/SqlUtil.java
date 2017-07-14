package com.github.vkorobkov.jfixtures.util;

import java.util.Objects;

public class SqlUtil {
    static final String SINGLE_QUOTE = "'";
    static final String SINGLE_QUOTE_ESCAPED = "''";

    public static String escapeString(String string) {
        string = string.replace(SINGLE_QUOTE, SINGLE_QUOTE_ESCAPED);
        return SqlUtil.surround(string, SINGLE_QUOTE);
    }

    public static String surround(String string, String with) {
        with = Objects.requireNonNull(with, "Can not surround string with null values");
        return
            with +
            Objects.requireNonNull(string, "The input string can not be null") +
            with;
    }
}