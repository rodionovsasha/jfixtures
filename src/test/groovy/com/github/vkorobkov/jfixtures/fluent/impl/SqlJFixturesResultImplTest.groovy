package com.github.vkorobkov.jfixtures.fluent.impl

import com.github.vkorobkov.jfixtures.fluent.JFixturesResult
import com.github.vkorobkov.jfixtures.sql.Appender
import com.github.vkorobkov.jfixtures.sql.SqlBase
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import com.github.vkorobkov.jfixtures.testutil.SqBaseTestImpl
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

import java.nio.file.Path

class SqlJFixturesResultImplTest extends Specification implements YamlVirtualDirectory {
    Path tmlDirectoryPath
    String outputPath
    SqlBase sql
    Appender appender
    JFixturesResult fixturesResult

    def EXPECTED_SQL = """DELETE FROM [users];
            |INSERT INTO [users] ([id], [name], [age]) VALUES (1, 'Vlad', 29);
            |""".stripMargin()

    void setup() {
        sql = Spy(SqBaseTestImpl)
        appender = new StringAppender()

        tmlDirectoryPath = unpackYamlToTempDirectory("default.yml")
        outputPath = tmlDirectoryPath.resolve("out.sql") as String

        fixturesResult = new SqlJFixturesResultImpl(tmlDirectoryPath as String, sql)
    }

    void cleanup() {
        tmlDirectoryPath.toFile().deleteDir()
    }

    def "processes fixtures to string"() {
        expect:
        fixturesResult.asString() == EXPECTED_SQL
    }

    def "second transformation to string returns the same result"() {
        expect:
        fixturesResult.asString() == EXPECTED_SQL

        and:
        fixturesResult.asString() == EXPECTED_SQL
    }

    def "saves result to file"() {
        when:
        fixturesResult.toFile(outputPath)

        then:
        new File(outputPath).text == EXPECTED_SQL
    }

    def "save to file overrides previous file"() {
        given:
        fixturesResult.toFile(outputPath)

        when:
        fixturesResult.toFile(outputPath)

        then:
        new File(outputPath).text == EXPECTED_SQL
    }

    def "throws IOException on empty file path"() {
        when:
        fixturesResult.toFile("")

        then:
        thrown(IOException)
    }
}
