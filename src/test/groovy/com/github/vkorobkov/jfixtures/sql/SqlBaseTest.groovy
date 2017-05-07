package com.github.vkorobkov.jfixtures.sql

import com.github.vkorobkov.jfixtures.instructions.CleanTable
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
        sql.cleanTable(appender, new CleanTable("users"))

        then:
        appender as String == "DELETE FROM [users];\n"
    }

    def "clean table with schema"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users"))

        then:
        appender as String == "DELETE FROM [admin].[users];\n"
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
        appender as String == "INSERT INTO [admin].[users] ([id], [name], [age]) VALUES (1, 'Vlad', 29);\n"
    }

    def "rethrows exception of appender on clean table"() {
        given:
        appender = Spy(StringAppender)
        1 * appender.append(_ as CharSequence) >> { text -> throw new IOException() }

        when:
        sql.cleanTable(appender, new CleanTable("users"))

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
        sql.escapeValue(FixtureValue.ofAuto("Vlad")) == "'Vlad'"
    }

    def "escaped single quite in string value"() {
        expect:
        sql.escapeValue(FixtureValue.ofAuto("Vlad' bug")) == "'Vlad'' bug'"
    }

    def "does not escape non string values"(unescaped, escaped) {
        expect:
        sql.escapeValue(FixtureValue.ofAuto(unescaped)) == escaped

        where:
        unescaped | escaped
        true | 'true'
        40 | '40'
        3.14 | '3.14'
    }

    def "does not escape SQL value"() {
        expect:
        sql.escapeValue(FixtureValue.ofSql("SELECT 1")) == "SELECT 1"
    }
}
