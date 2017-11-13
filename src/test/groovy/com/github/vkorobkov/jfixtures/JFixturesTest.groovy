package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.sql.SqlType
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class JFixturesTest extends Specification implements YamlVirtualFolder {
    Path tmpFolderPath
    String outputPath
    String outputXmlPath

    def DEFAULT_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age", "role", "hobby") VALUES (1, 'Vlad', 29, NULL, NULL);
            |""".stripMargin()
    def MYSQL_EXPECTED_SQL = """DELETE FROM `users`;
            |INSERT INTO `users` (`id`, `name`, `age`, `role`, `hobby`) VALUES (1, 'Vlad', 29, NULL, NULL);
            |""".stripMargin()
    def MSSQL_EXPECTED_SQL = """DELETE FROM [users];
            |INSERT INTO [users] ([id], [name], [age], [role], [hobby]) VALUES (1, 'Vlad', 29, NULL, NULL);
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
            |            <entry>
            |                <key>role</key>
            |                <value type="AUTO">null</value>
            |            </entry>
            |            <entry>
            |                <key>hobby</key>
            |                <value type="AUTO">null</value>
            |            </entry>
            |        </values>
            |    </instruction>
            |</instructions>
            |""".stripMargin()

    void setup() {
        tmpFolderPath = unpackYamlToTempFolder("default.yml")
        outputPath = tmpFolderPath.resolve("out.sql") as String
        outputXmlPath = tmpFolderPath.resolve("out.xml") as String
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
    }

    def "dummy constructor"() {
        expect:
        new JFixtures()
    }

    def "mysql fixture to string"() {
        expect:
        JFixtures.mysql(tmpFolderPath as String).asString() == MYSQL_EXPECTED_SQL
    }

    def "mysql fixture to file"() {
        when:
        JFixtures.mysql(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == MYSQL_EXPECTED_SQL
    }

    def "by dialect fixture to a string"() {
        expect:
        JFixtures.byDialect(tmpFolderPath as String, SqlType.SQL99).asString() == DEFAULT_EXPECTED_SQL
    }

    def "by dialect fixture to a file"() {
        when:
        JFixtures.byDialect(tmpFolderPath as String, SqlType.SQL99).toFile(outputPath)

        then:
        new File(outputPath).text == DEFAULT_EXPECTED_SQL
    }

    def "MSSQL fixture to a string"() {
        expect:
        JFixtures.msSql(tmpFolderPath as String).asString() == MSSQL_EXPECTED_SQL
    }

    def "MSSQL fixture to a file"() {
        when:
        JFixtures.msSql(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == MSSQL_EXPECTED_SQL
    }

    def "SQL99 fixture to a string"() {
        expect:
        JFixtures.sql99(tmpFolderPath as String).asString() == DEFAULT_EXPECTED_SQL
    }

    def "SQL99 fixture to a file"() {
        when:
        JFixtures.sql99(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == DEFAULT_EXPECTED_SQL
    }

    def "Fixture to an XML string"() {
        expect:
        JFixtures.xml(tmpFolderPath as String).asString() == DEFAULT_EXPECTED_XML
    }

    def "Fixture to an XML file"() {
        when:
        JFixtures.xml(tmpFolderPath as String).toFile(outputXmlPath)

        then:
        new File(outputXmlPath).text == DEFAULT_EXPECTED_XML
    }
}
