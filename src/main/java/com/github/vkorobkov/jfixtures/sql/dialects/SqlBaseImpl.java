package com.github.vkorobkov.jfixtures.sql.dialects;

import com.github.vkorobkov.jfixtures.sql.SqlBase;
import com.github.vkorobkov.jfixtures.util.SqlUtil;

public abstract class SqlBaseImpl implements SqlBase {
    @Override
    public String escapeTableOrColumnPart(String part) {
        return SqlUtil.surround(part, "\"");
    }
}