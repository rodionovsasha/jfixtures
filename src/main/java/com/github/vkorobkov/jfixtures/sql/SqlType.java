package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.sql.dialects.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SqlType {
    MYSQL(new MySql()),
    MSSQL(new MsSql()),
    SQL99(new Sql99());

    @Getter
    private final Sql sqlDialect;
}
