package com.github.vkorobkov.jfixtures;

import com.github.vkorobkov.jfixtures.fluent.JFixturesResult;
import com.github.vkorobkov.jfixtures.fluent.JFixturesResultImpl;
import com.github.vkorobkov.jfixtures.sql.dialects.MySql;
import com.github.vkorobkov.jfixtures.sql.dialects.PgSql;

public class JFixtures {
    public static JFixturesResult postgres(String fixturesFolder) {
        return new JFixturesResultImpl(fixturesFolder, new PgSql());
    }

    public static JFixturesResult mysql(String fixturesFolder) {
        return new JFixturesResultImpl(fixturesFolder, new MySql());
    }
}