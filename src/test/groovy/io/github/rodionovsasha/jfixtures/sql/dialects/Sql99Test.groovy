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

import java.time.Instant

@Unroll
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

    def "clean table with cascading truncate"() {
        when:
        sql.cleanTable(appender, new CleanTable("admin.users", CleanMethod.TRUNCATE_CASCADE))

        then:
        appender as String == 'TRUNCATE TABLE "admin"."users" CASCADE;\n'
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
                id  : Value.of(1),
                name: Value.of("Vlad"),
                age : Value.of(29),
                hobby : Value.ofNull(),
                active : Value.of(true)
        ])

        when:
        sql.insertRow(appender, insertRow)

        then:
        appender as String == 'INSERT INTO "admin"."users" ("id", "name", "age", "hobby", "active") VALUES (1, \'Vlad\', 29, NULL, TRUE);\n'
    }

    def "renders binary values and dates as valid SQL literals"() {
        given:
        def insertRow = new InsertRow("files", "logo", [
                binary    : Value.of([0x0a, 0xff] as byte[]),
                date      : Value.of(Date.from(Instant.parse("2001-11-23T00:00:00Z"))),
                timestamp : Value.of(Date.from(Instant.parse("2001-11-23T15:02:31Z")))
        ])

        when:
        sql.insertRow(appender, insertRow)

        then:
        appender as String == 'INSERT INTO "files" ("binary", "date", "timestamp") VALUES (X\'0aff\', \'2001-11-23\', \'2001-11-23 15:02:31\');\n'
    }
}
