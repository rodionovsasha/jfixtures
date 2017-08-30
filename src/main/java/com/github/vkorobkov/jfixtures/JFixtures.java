package com.github.vkorobkov.jfixtures;

import com.github.vkorobkov.jfixtures.fluent.JFixturesResult;
import com.github.vkorobkov.jfixtures.fluent.impl.SqlJFixturesResultImpl;
import com.github.vkorobkov.jfixtures.fluent.impl.XmlJFixturesResultImpl;
import com.github.vkorobkov.jfixtures.sql.SqlType;
import com.github.vkorobkov.jfixtures.sql.dialects.MsSql;
import com.github.vkorobkov.jfixtures.sql.dialects.MySql;
import com.github.vkorobkov.jfixtures.sql.dialects.Sql99;

public final class JFixtures {
    private JFixtures() {
    }

    public static JFixturesResult mysql(String fixturesFolder) {
        return new SqlJFixturesResultImpl(fixturesFolder, new MySql());
    }

    public static JFixturesResult msSql(String fixturesFolder) {
        return new SqlJFixturesResultImpl(fixturesFolder, new MsSql());
    }

    public static JFixturesResult sql99(String fixturesFolder) {
        return new SqlJFixturesResultImpl(fixturesFolder, new Sql99());
    }

    public static JFixturesResult byDialect(String fixturesFolder, SqlType sqlType) {
        return new SqlJFixturesResultImpl(fixturesFolder, sqlType.getSqlDialect());
    }

    public static JFixturesResult xml(String fixturesFolder) {
        return new XmlJFixturesResultImpl(fixturesFolder);
    }
}
