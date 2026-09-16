package com.github.rodionovsasha.jfixtures.sql;

import com.github.rodionovsasha.jfixtures.sql.dialects.MicrosoftSql;
import com.github.rodionovsasha.jfixtures.sql.dialects.MySql;
import com.github.rodionovsasha.jfixtures.sql.dialects.Sql99;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum SqlType {
    MYSQL(new MySql()),
    MICROSOFT_SQL(new MicrosoftSql()),
    SQL99(new Sql99());

    @Getter
    private final Sql sqlDialect;
}
