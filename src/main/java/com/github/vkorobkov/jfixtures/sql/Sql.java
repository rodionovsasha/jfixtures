package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;

public interface Sql {
    void cleanTable(Appender appendable, CleanTable cleanTable);
    void insertRow(Appender appendable, InsertRow insertRow);
}
