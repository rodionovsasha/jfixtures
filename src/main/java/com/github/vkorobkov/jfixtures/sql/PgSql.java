package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.loader.FixtureValue;
import com.github.vkorobkov.jfixtures.util.SqlUtil;

public class PgSql implements SqlBase {
    @Override
    public String escapeTableOrColumnPart(String part) {
        return SqlUtil.surround(part, "\"");
    }

    @Override
    public String escapeValue(FixtureValue value) {
        String str = value.toString();
        return value.isString() ? SqlUtil.escapeString(str) : str;
    }
}
