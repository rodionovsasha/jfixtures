package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.sql.dialects.*;
import lombok.Getter;

public enum SqlType {
    POSTGRES(new PgSql()),
    MYSQL(new MySql()),
    H2(new H2()),
    CLICKHOUSE(new ClickHouse()),
    MSSQL(new MsSql()),
    SYBASE(new SybaseSql()),
    ORACLE(new OracleSql());

    @Getter
    private final Sql sqlDialect;

    SqlType(Sql sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
}
