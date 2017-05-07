package com.github.vkorobkov.jfixtures.sql.dialects

import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.loader.FixtureValue
import com.github.vkorobkov.jfixtures.sql.Appender
import com.github.vkorobkov.jfixtures.sql.Sql
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import spock.lang.Specification

class PgSqlTest extends Specification {

    Sql sql
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

    def "clean table"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users"))

        then:
        appender as String == 'DELETE FROM "admin"."users";\n'
    }

    def "insert row test"() {
        given:
        def insertRow = new InsertRow("admin.users", "vlad", [
            id: FixtureValue.ofAuto(1),
            name: FixtureValue.ofAuto("Vlad"),
            age : FixtureValue.ofAuto(29)
        ])

        when:
        sql.insertRow(appender, insertRow)

        then:
        appender as String == 'INSERT INTO "admin"."users" ("id", "name", "age") VALUES (1, \'Vlad\', 29);\n'
    }
}
