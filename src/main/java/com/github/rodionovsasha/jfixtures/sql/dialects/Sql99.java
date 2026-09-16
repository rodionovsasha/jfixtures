package com.github.rodionovsasha.jfixtures.sql.dialects;

import com.github.rodionovsasha.jfixtures.sql.SqlBase;
import com.github.rodionovsasha.jfixtures.util.SqlUtil;

public class Sql99 implements SqlBase {
    @Override
    public String escapeTableOrColumnPart(String part) {
        return SqlUtil.surround(part, "\"");
    }
}
