package io.github.rodionovsasha.jfixtures.sql;

import io.github.rodionovsasha.jfixtures.instructions.CleanTable;
import io.github.rodionovsasha.jfixtures.instructions.CustomSql;
import io.github.rodionovsasha.jfixtures.instructions.InsertRow;

public interface Sql {
    void cleanTable(Appender appendable, CleanTable cleanTable);

    void insertRow(Appender appendable, InsertRow insertRow);

    void addCustomSql(Appender appendable, CustomSql customSql);
}
