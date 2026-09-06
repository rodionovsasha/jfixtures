package com.github.rodionovsasha.jfixtures.sql;

import com.github.rodionovsasha.jfixtures.instructions.CleanTable;
import com.github.rodionovsasha.jfixtures.instructions.CustomSql;
import com.github.rodionovsasha.jfixtures.instructions.InsertRow;

public interface Sql {
    void cleanTable(Appender appendable, CleanTable cleanTable);

    void insertRow(Appender appendable, InsertRow insertRow);

    void addCustomSql(Appender appendable, CustomSql customSql);
}
