package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.JFixtures
import com.github.vkorobkov.jfixtures.sql.SqlType
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path

@Unroll
class JFixturesIntegrationTest extends Specification implements YamlVirtualDirectory {
    Path tmpDirectoryPath
    String outputPath
    String outputXmlPath

    static DEFAULT_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (1, 'Vlad', 29);
            |""".stripMargin()
    static MYSQL_EXPECTED_SQL = """DELETE FROM `users`;
            |INSERT INTO `users` (`id`, `name`, `age`) VALUES (1, 'Vlad', 29);
            |""".stripMargin()
    static MICROSOFT_SQL_EXPECTED_SQL = """DELETE FROM [users];
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

    def mapOfTables = [
            users: [
                    vlad: [id: 1, name: 'Vlad', age: 29]
            ]
    ]

    void setup() {
        tmpDirectoryPath = unpackYamlToTempDirectory("default.yml")
        outputPath = tmpDirectoryPath.resolve("out.sql") as String
        outputXmlPath = tmpDirectoryPath.resolve("out.xml") as String
    }

    void cleanup() {
        tmpDirectoryPath.toFile().deleteDir()
    }

    def "mysql fixture to string"() {
        when:
        def sql = JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toMySql()
                .toString()

        then:
        sql == MYSQL_EXPECTED_SQL
    }

    def "mysql fixture to file"() {
        when:
        JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toMySql()
                .toFile(outputPath)

        then:
        new File(outputPath).text == MYSQL_EXPECTED_SQL
    }

    def "by dialect fixture to a string where dialect is #type"(SqlType type, String expectedSql) {
        when:
        def sql = JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toSql(type)
                .toString()

        then:
        sql == expectedSql

        where:
        type << SqlType.values()
        expectedSql << [MYSQL_EXPECTED_SQL, MICROSOFT_SQL_EXPECTED_SQL, DEFAULT_EXPECTED_SQL]
    }

    def "by dialect fixture to a file where dialect is #type"(SqlType type, String expectedSql) {
        when:
        JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toSql(type)
                .toFile(outputPath)

        then:
        new File(outputPath).text == expectedSql

        where:
        type << SqlType.values()
        expectedSql << [MYSQL_EXPECTED_SQL, MICROSOFT_SQL_EXPECTED_SQL, DEFAULT_EXPECTED_SQL]
    }

    def "Microsoft SQL fixture to a string"() {
        when:
        def sql = JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toMicrosoftSql()
                .toString()

        then:
        sql == MICROSOFT_SQL_EXPECTED_SQL
    }

    def "Microsoft SQL fixture to a file"() {
        when:
        JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toMicrosoftSql()
                .toFile(outputPath)

        then:
        new File(outputPath).text == MICROSOFT_SQL_EXPECTED_SQL
    }

    def "SQL99 fixture to a string"() {
        when:
        def sql = JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toSql99()
                .toString()

        then:
        sql == DEFAULT_EXPECTED_SQL
    }

    def "SQL99 fixture to a file"() {
        when:
        JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toSql99()
                .toFile(outputPath)

        then:
        new File(outputPath).text == DEFAULT_EXPECTED_SQL
    }

    def "Fixture to an XML string"() {
        when:
        def xml = JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toXml()
                .toString()

        then:
        xml == DEFAULT_EXPECTED_XML
    }

    def "Fixture to an XML file"() {
        when:
        JFixtures
                .noConfig()
                .loadDirectory(tmpDirectoryPath)
                .compile()
                .toXml()
                .toFile(outputXmlPath)

        then:
        new File(outputXmlPath).text == DEFAULT_EXPECTED_XML
    }

    def "Microsoft SQL fixture from a Map to a string"() {
        when:
        def sql = JFixtures
                .noConfig()
                .addTables(mapOfTables)
                .compile()
                .toMicrosoftSql()
                .toString()

        then:
        sql == MICROSOFT_SQL_EXPECTED_SQL
    }

    def "SQL99 fixture from a Map to a string"() {
        when:
        def sql = JFixtures
                .noConfig()
                .addTables(mapOfTables)
                .compile()
                .toSql99()
                .toString()

        then:
        sql == DEFAULT_EXPECTED_SQL
    }

    def "Fixture from a Map to an XML string"() {
        when:
        def xml = JFixtures
                .noConfig()
                .addTables(mapOfTables)
                .compile()
                .toXml()
                .toString()

        then:
        xml == DEFAULT_EXPECTED_XML
    }
}
