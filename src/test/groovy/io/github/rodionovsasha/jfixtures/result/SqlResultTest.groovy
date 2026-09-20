package io.github.rodionovsasha.jfixtures.result

import io.github.rodionovsasha.jfixtures.config.structure.tables.CleanMethod
import io.github.rodionovsasha.jfixtures.domain.Value
import io.github.rodionovsasha.jfixtures.instructions.CleanTable
import io.github.rodionovsasha.jfixtures.instructions.InsertRow
import io.github.rodionovsasha.jfixtures.sql.SqlType
import io.github.rodionovsasha.jfixtures.sql.SqlFormatting
import io.github.rodionovsasha.jfixtures.sql.appenders.StringAppender
import io.github.rodionovsasha.jfixtures.testutil.Assertions
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class SqlResultTest extends Specification implements Assertions {

    @Shared
    def instructions = [
        new CleanTable("users", CleanMethod.DELETE),
        new InsertRow("users", "vlad", [
            id: Value.of(1),
            name: Value.of("Vlad"),
            age: Value.of(30)
        ])
    ]

    @Shared
    def subject = new SqlResult(instructions, SqlType.SQL99.sqlDialect)

    def EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (1, 'Vlad', 30);
            |""".stripMargin()

    def "::constructor saves instructions and sql implementation"() {
        expect:
        assertCollectionsEqual(subject.instructions, instructions)

        and:
        subject.sql == SqlType.SQL99.sqlDialect
    }

    def "::constructor saves instructions as unmodifiable collection"() {
        expect:
        assertUnmodifiableCollection(subject.instructions)
    }

    def "::toString returns string representation of output SQL"() {
        expect:
        subject.toString() == EXPECTED_SQL
    }

    def "::toFile writes output SQL to file"() {
        setup:
        def file = createTempOutputFile()

        when:
        subject.toFile(file.toString())

        then:
        file.text == EXPECTED_SQL

        cleanup:
        file.toFile().delete()
    }

    def "::toFile overwrites file when exists"() {
        setup:
        def file = createTempOutputFile()

        when:
        2.times {
            subject.toFile(file.toString())
        }

        then:
        file.text == EXPECTED_SQL

        cleanup:
        file.toFile().delete()
    }

    def "::toFile does not shallow IO exceptions"() {
        when:
        subject.toFile("")

        then:
        thrown(IOException)
    }

    def "::applyAppender applies any custom appender"() {
        given:
        def appender = new StringAppender();

        when:
        subject.applyAppender(appender)

        then:
        appender.toString() == EXPECTED_SQL
    }

    def "renders SQL with configured line and statement separators"() {
        expect:
        subject.withFormatting(new SqlFormatting("\r\n", 1)).toString() == EXPECTED_SQL.replace("\n", "\r\n\r\n")
    }

    def "writes configured formatting to a file"() {
        given:
        def file = createTempOutputFile()

        when:
        subject.withFormatting(new SqlFormatting("\r\n", 1)).toFile(file.toString())

        then:
        file.text == EXPECTED_SQL.replace("\n", "\r\n\r\n")

        cleanup:
        file.toFile().delete()
    }

    def "rejects invalid SQL formatting"() {
        when:
        new SqlFormatting("", 0)

        then:
        thrown(IllegalArgumentException)

        when:
        new SqlFormatting("\n", -1)

        then:
        thrown(IllegalArgumentException)

        when:
        new SqlFormatting(null, 0)

        then:
        thrown(IllegalArgumentException)
    }

    private static createTempOutputFile() {
        Files.createTempFile("jfixtures", "output.sql")
    }
}
