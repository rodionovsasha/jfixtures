package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.sql.dialects.*;
import lombok.Getter;

public enum SqlType {
    MYSQL(new MySql()),
    MSSQL(new MsSql()),
    SQL99(new Sql99());

    @Getter
    private final Sql sqlDialect;

    SqlType(Sql sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
}
