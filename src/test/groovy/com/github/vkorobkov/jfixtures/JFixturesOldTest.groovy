package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.sql.SqlType
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

import java.nio.file.Path

class JFixturesOldTest extends Specification implements YamlVirtualDirectory {
    Path tmpDirectoryPath
    String outputPath
    String outputXmlPath

    def DEFAULT_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (1, 'Vlad', 29);
            |""".stripMargin()
    def MYSQL_EXPECTED_SQL = """DELETE FROM `users`;
            |INSERT INTO `users` (`id`, `name`, `age`) VALUES (1, 'Vlad', 29);
            |""".stripMargin()
    def MICROSOFT_SQL_EXPECTED_SQL = """DELETE FROM [users];
            |INSERT INTO [users] ([id], [name], [age]) VALUES (1, 'Vlad', 29);
            |""".stripMargin()
    def DEFAULT_EXPECTED_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            |<instructions>
            |    <instruction type="CleanTable" table="users" cleanMethod="DELETE"/>
            |    <instruction type="InsertRow" table="users" rowName="vlad">
            |        <values>
            |            <entry>
            |                <key>id</key>
            |                <value type="AUTO">1</value>
            |            </entry>
            |            <entry>
            |                <key>name</key>
            |                <value type="TEXT">Vlad</value>
            |            </entry>
            |            <entry>
            |                <key>age</key>
            |                <value type="AUTO">29</value>
            |            </entry>
            |        </values>
            |    </instruction>
            |</instructions>
            |""".stripMargin()

    void setup() {
        tmpDirectoryPath = unpackYamlToTempDirectory("default.yml")
        outputPath = tmpDirectoryPath.resolve("out.sql") as String
        outputXmlPath = tmpDirectoryPath.resolve("out.xml") as String
    }

    void cleanup() {
        tmpDirectoryPath.toFile().deleteDir()
    }

    def "dummy constructor"() {
        expect:
        new JFixturesOld()
    }

    def "mysql fixture to string"() {
        expect:
        JFixturesOld.mysql(tmpDirectoryPath as String).asString() == MYSQL_EXPECTED_SQL
    }

    def "mysql fixture to file"() {
        when:
        JFixturesOld.mysql(tmpDirectoryPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == MYSQL_EXPECTED_SQL
    }

    def "by dialect fixture to a string"() {
        expect:
        JFixturesOld.byDialect(tmpDirectoryPath as String, SqlType.SQL99).asString() == DEFAULT_EXPECTED_SQL
    }

    def "by dialect fixture to a file"() {
        when:
        JFixturesOld.byDialect(tmpDirectoryPath as String, SqlType.SQL99).toFile(outputPath)

        then:
        new File(outputPath).text == DEFAULT_EXPECTED_SQL
    }

    def "Microsoft SQL fixture to a string"() {
        expect:
        JFixturesOld.microsoftSql(tmpDirectoryPath as String).asString() == MICROSOFT_SQL_EXPECTED_SQL
    }

    def "Microsoft SQL fixture to a file"() {
        when:
        JFixturesOld.microsoftSql(tmpDirectoryPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == MICROSOFT_SQL_EXPECTED_SQL
    }

    def "SQL99 fixture to a string"() {
        expect:
        JFixturesOld.sql99(tmpDirectoryPath as String).asString() == DEFAULT_EXPECTED_SQL
    }

    def "SQL99 fixture to a file"() {
        when:
        JFixturesOld.sql99(tmpDirectoryPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == DEFAULT_EXPECTED_SQL
    }

    def "Fixture to an XML string"() {
        expect:
        JFixturesOld.xml(tmpDirectoryPath as String).asString() == DEFAULT_EXPECTED_XML
    }

    def "Fixture to an XML file"() {
        when:
        JFixturesOld.xml(tmpDirectoryPath as String).toFile(outputXmlPath)

        then:
        new File(outputXmlPath).text == DEFAULT_EXPECTED_XML
    }
}
