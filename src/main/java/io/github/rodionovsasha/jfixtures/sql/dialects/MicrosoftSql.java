package io.github.rodionovsasha.jfixtures.sql.dialects;

import io.github.rodionovsasha.jfixtures.sql.SqlBase;
import io.github.rodionovsasha.jfixtures.util.SqlUtil;

public class MicrosoftSql implements SqlBase {
    @Override
    public String escapeTableOrColumnPart(String part) {
        return SqlUtil.surround(part, "[", "]");
    }
}
