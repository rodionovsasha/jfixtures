package com.github.vkorobkov.jfixtures.result

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod
import com.github.vkorobkov.jfixtures.domain.Value
import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.sql.SqlType
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import com.github.vkorobkov.jfixtures.testutil.Assertions
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

    private static createTempOutputFile() {
        Files.createTempFile("jfixtures", "output.sql")
    }
}
