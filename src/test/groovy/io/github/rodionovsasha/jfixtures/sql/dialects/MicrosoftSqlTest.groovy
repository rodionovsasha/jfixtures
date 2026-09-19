package io.github.rodionovsasha.jfixtures.sql.dialects

import io.github.rodionovsasha.jfixtures.config.structure.tables.CleanMethod
import io.github.rodionovsasha.jfixtures.domain.Value
import io.github.rodionovsasha.jfixtures.instructions.CleanTable
import io.github.rodionovsasha.jfixtures.instructions.InsertRow
import io.github.rodionovsasha.jfixtures.sql.Appender
import io.github.rodionovsasha.jfixtures.sql.Sql
import io.github.rodionovsasha.jfixtures.sql.appenders.StringAppender
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MicrosoftSqlTest extends Specification {
    Sql sql
    Appender appender

    void setup() {
        sql = new MicrosoftSql()
        appender = new StringAppender()
    }

    def "escapes schema, table and columns with quotes"(unescaped, escaped) {
        expect:
        sql.escapeTableOrColumn(unescaped) == escaped

        where:
        unescaped        | escaped
        "users"          | '[users]'
        "admin.users"    | '[admin].[users]'
        "admin.users.id" | '[admin].[users].[id]'
    }

    def "clean table"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users", CleanMethod.DELETE))

        then:
        appender as String == 'DELETE FROM [admin].[users];\n'
    }

    def "clean table with truncate"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users", CleanMethod.TRUNCATE))

        then:
        appender as String == 'TRUNCATE TABLE [admin].[users];\n'
    }

    def "clean table with cascading truncate"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users", CleanMethod.TRUNCATE_CASCADE))

        then:
        appender as String == 'TRUNCATE TABLE [admin].[users] CASCADE;\n'
    }

    def "insert row test"() {
        given:
        def insertRow = new InsertRow("admin.users", "vlad", [
                id  : Value.of(1),
                name: Value.of("Vlad"),
                age : Value.of(29)
        ])

        when:
        sql.insertRow(appender, insertRow)

        then:
        appender as String == 'INSERT INTO [admin].[users] ([id], [name], [age]) VALUES (1, \'Vlad\', 29);\n'
    }
}
