package com.github.vkorobkov.jfixtures.sql

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod
import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.CustomSql
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.loader.FixtureValue
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import com.github.vkorobkov.jfixtures.testutil.SqBaseTestImpl
import spock.lang.Specification

class SqlBaseTest extends Specification {
    SqlBase sql
    Appender appender

    void setup() {
        sql = Spy(SqBaseTestImpl)
        appender = new StringAppender()
    }

    def "escape table without schema"() {
        expect:
        sql.escapeTableOrColumn("users") == "[users]"
    }

    def "escape table with schema"() {
        expect:
        sql.escapeTableOrColumn("admin.users") == "[admin].[users]"
    }

    def "escape table with schema and with column"() {
        expect:
        sql.escapeTableOrColumn("admin.users.id") == "[admin].[users].[id]"
    }

    def "clean table without schema"() {
        when:
        sql.cleanTable(appender, new CleanTable("users", CleanMethod.DELETE))

        then:
        appender as String == "DELETE FROM [users];\n"
    }

    def "clean table with schema"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users", CleanMethod.DELETE))

        then:
        appender as String == "DELETE FROM [admin].[users];\n"
    }

    def "clean table with truncate"() {
        when:
        sql.cleanTable(appender, new CleanTable("users", CleanMethod.TRUNCATE))

        then:
        appender as String == "TRUNCATE TABLE [users];\n"
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
                age : new FixtureValue(29)
        ])

        when:
        sql.insertRow(appender, insertRow)

        then:
        appender as String == "INSERT INTO [admin].[users] ([id], [name], [age]) VALUES (1, 'Vlad', 29);\n"
    }

    def "rethrows exception of appender on clean table"() {
        given:
        appender = Spy(StringAppender)
        1 * appender.append(_ as CharSequence) >> { text -> throw new IOException() }

        when:
        sql.cleanTable(appender, new CleanTable("users", CleanMethod.DELETE))

        then:
        thrown(IOException)
    }

    def "rethrows exception of appender on insert row"() {
        given:
        appender = Spy(StringAppender)
        1 * appender.append(_ as CharSequence) >> { text -> throw new IOException() }

        when:
        sql.insertRow(appender, new InsertRow("users", "vlad", [:]))

        then:
        thrown(IOException)
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
        true      | 'true'
        40        | '40'
        3.14      | '3.14'
    }

    def "does not escape SQL value"() {
        expect:
        sql.escapeValue(new FixtureValue("sql:SELECT 1")) == "SELECT 1"
    }

    def "add Custom Sql test"() {
        given:
        def addCustomSql = new CustomSql("users", "BEGIN TRANSACTION;")

        when:
        sql.addCustomSql(appender, addCustomSql)

        then:
        appender as String == "BEGIN TRANSACTION;\n"
    }

    def "rethrows exception of appender on add Custom Sql"() {
        given:
        appender = Spy(StringAppender)
        1 * appender.append(_ as CharSequence) >> { text -> throw new IOException() }

        when:
        sql.addCustomSql(appender, new CustomSql("users", "BEGIN TRANSACTION;"))

        then:
        thrown(IOException)
    }

}
