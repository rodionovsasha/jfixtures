package com.github.vkorobkov.jfixtures.sql.dialects

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod
import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.loader.FixtureValue
import com.github.vkorobkov.jfixtures.sql.Appender
import com.github.vkorobkov.jfixtures.sql.Sql
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import spock.lang.Specification

class Sql99Test extends Specification {
    Sql sql
    Appender appender

    void setup() {
        sql = new Sql99()
        appender = new StringAppender()
    }

    def "escapes schema, table and columns with quotes"(unescaped, escaped) {
        expect:
        sql.escapeTableOrColumn(unescaped) == escaped

        where:
        unescaped        | escaped
        "users"          | '"users"'
        "admin.users"    | '"admin"."users"'
        "admin.users.id" | '"admin"."users"."id"'
    }

    def "clean table with delete"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users", CleanMethod.DELETE))

        then:
        appender as String == 'DELETE FROM "admin"."users";\n'
    }

    def "clean table with truncate"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users", CleanMethod.TRUNCATE))

        then:
        appender as String == 'TRUNCATE TABLE "admin"."users";\n'
    }

    def "no clean table with none"() {
        when:
        sql.cleanTable(appender, new CleanTable("users", CleanMethod.NONE))

        then:
        appender as String == ""
    }

    def "insert row test"() {
        given:
        def insertRow = new InsertRow("admin.users", "vlad", [
                id  : new FixtureValue(1),
                name: new FixtureValue("Vlad"),
                age : new FixtureValue(29),
                hobby : new FixtureValue(null),
        ])

        when:
        sql.insertRow(appender, insertRow)

        then:
        appender as String == 'INSERT INTO "admin"."users" ("id", "name", "age", "hobby") VALUES (1, \'Vlad\', 29, NULL);\n'
    }
}
