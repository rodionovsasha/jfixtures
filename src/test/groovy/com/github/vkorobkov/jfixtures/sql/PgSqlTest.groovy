package com.github.vkorobkov.jfixtures.sql

import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.loader.FixtureValue
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import spock.lang.Specification

class PgSqlTest extends Specification {

    PgSql sql
    Appender appender

    void setup() {
        sql = new PgSql()
        appender = new StringAppender()
    }

    def "escapes schema, table and columns with quotes"(unescaped, escaped) {
        expect:
        sql.escapeTableOrColumn(unescaped) == escaped

        where:
        unescaped | escaped
        "users" | '"users"'
        "admin.users" | '"admin"."users"'
        "admin.users.id" | '"admin"."users"."id"'
    }

    def "escapes string values with single quote"() {
        expect:
        sql.escapeValue(new FixtureValue("Vlad")) == "'Vlad'"
    }

    def "escaped single quite in string value"() {
        expect:
        sql.escapeValue(new FixtureValue("Vlad' bug")) == "'Vlad'' bug'"
    }

    def "does not escape non string values"(unescaped, escaped) {
        expect:
        sql.escapeValue(new FixtureValue(unescaped)) == escaped

        where:
        unescaped | escaped
        true | 'true'
        40 | '40'
        3.14 | '3.14'
    }

    def "clean table"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users"))

        then:
        appender as String == 'DELETE FROM "admin"."users";\n'
    }

    def "insert row test"() {
        given:
        def insertRow = new InsertRow("admin.users", "vlad", [
            id: new FixtureValue(1),
            name: new FixtureValue("Vlad"),
            age : new FixtureValue(29)
        ])

        when:
        sql.insertRow(appender, insertRow)

        then:
        appender as String == 'INSERT INTO "admin"."users" ("id", "name", "age") VALUES (1, \'Vlad\', 29);\n'
    }
}
