package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.loader.FixtureValue;

public class PgSql implements SqlBase {
    @Override
    public String escapeTableOrColumnPart(String part) {
        return surround(part, "\"");
    }

    @Override
    public String escapeValue(FixtureValue value) {
        String str = value.toString();
        return value.isString() ? surround(str, "'") : str;
    }

    private String surround(String s, String with) {
        return with + s + with;
    }
}
