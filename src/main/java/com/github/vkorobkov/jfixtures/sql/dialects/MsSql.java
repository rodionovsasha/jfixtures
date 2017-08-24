package com.github.vkorobkov.jfixtures.sql.dialects;

import com.github.vkorobkov.jfixtures.util.SqlUtil;

public class MsSql extends SqlBaseImpl {
    @Override
    public String escapeTableOrColumnPart(String part) {
        return SqlUtil.surround(part, "[", "]");
    }
}
