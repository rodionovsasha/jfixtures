package com.github.vkorobkov.jfixtures.fluent.impl;

import com.github.vkorobkov.jfixtures.sql.Appender;
import com.github.vkorobkov.jfixtures.sql.Sql;
import com.github.vkorobkov.jfixtures.sql.SqlBridge;
import com.github.vkorobkov.jfixtures.sql.appenders.FileAppender;
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender;
import com.github.vkorobkov.jfixtures.util.WithResource;

public class SqlJFixturesResultImpl extends JFixturesResultImpl {
    private final Sql sql;

    public SqlJFixturesResultImpl(String fixturesFolder, Sql sql) {
        super(fixturesFolder);
        this.sql = sql;
    }

    private <T extends Appender> T applyAppender(T appender) {
        new SqlBridge(sql, appender).apply(getInstructions());
        return appender;
    }

    @Override
    public String asString() {
        return applyAppender(new StringAppender()).toString();
    }

    @Override
    public void toFile(String name) {
        WithResource.touch(() -> new FileAppender(name), this::applyAppender);
    }
}
