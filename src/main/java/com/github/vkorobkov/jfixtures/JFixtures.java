package com.github.vkorobkov.jfixtures;

import com.github.vkorobkov.jfixtures.fluent.JFixturesResult;
import com.github.vkorobkov.jfixtures.fluent.JFixturesResultImpl;
import com.github.vkorobkov.jfixtures.sql.SqlType;
import com.github.vkorobkov.jfixtures.sql.dialects.*;

public final class JFixtures {
    private JFixtures() {
    }

    public static JFixturesResult postgres(String fixturesFolder) {
        return new JFixturesResultImpl(fixturesFolder, new PgSql());
    }

    public static JFixturesResult mysql(String fixturesFolder) {
        return new JFixturesResultImpl(fixturesFolder, new MySql());
    }

    public static JFixturesResult h2(String fixturesFolder) {
        return new JFixturesResultImpl(fixturesFolder, new H2());
    }

    public static JFixturesResult clickHouse(String fixturesFolder) {
        return new JFixturesResultImpl(fixturesFolder, new ClickHouse());
    }

    public static JFixturesResult oracle(String fixturesFolder) {
        return new JFixturesResultImpl(fixturesFolder, new OracleSql());
    }

    public static JFixturesResult msSql(String fixturesFolder) {
        return new JFixturesResultImpl(fixturesFolder, new MsSql());
    }

    public static JFixturesResult byDialect(String fixturesFolder, SqlType sqlType) {
        return new JFixturesResultImpl(fixturesFolder, sqlType.getSqlDialect());
    }
}
