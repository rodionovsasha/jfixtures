package com.github.vkorobkov.jfixtures.types

import com.github.vkorobkov.jfixtures.JFixtures
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

import java.nio.file.Path

class JFixturesNullTest extends Specification implements YamlVirtualDirectory {
    Path nullDirectoryPath
    Path stringDirectoryPath

    def NULL_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age", "role", "hobby") VALUES (1, NULL, NULL, NULL, NULL);
            |""".stripMargin()
    def STRING_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age", "role", "hobby") VALUES (2, '~', 'null', 'Null', 'nULL');
            |""".stripMargin()
    def NULL_EXPECTED_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            |<instructions>
            |    <instruction type="CleanTable" table="users" cleanMethod="DELETE"/>
            |    <instruction type="InsertRow" table="users" rowName="homer">
            |        <values>
            |            <entry>
            |                <key>id</key>
            |                <value type="AUTO">1</value>
            |            </entry>
            |            <entry>
            |                <key>name</key>
            |                <value type="AUTO">null</value>
            |            </entry>
            |            <entry>
            |                <key>age</key>
            |                <value type="AUTO">null</value>
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

    def STRING_EXPECTED_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            |<instructions>
            |    <instruction type="CleanTable" table="users" cleanMethod="DELETE"/>
            |    <instruction type="InsertRow" table="users" rowName="bart">
            |        <values>
            |            <entry>
            |                <key>id</key>
            |                <value type="AUTO">2</value>
            |            </entry>
            |            <entry>
            |                <key>name</key>
            |                <value type="TEXT">~</value>
            |            </entry>
            |            <entry>
            |                <key>age</key>
            |                <value type="TEXT">null</value>
            |            </entry>
            |            <entry>
            |                <key>role</key>
            |                <value type="TEXT">Null</value>
            |            </entry>
            |            <entry>
            |                <key>hobby</key>
            |                <value type="TEXT">nULL</value>
            |            </entry>
            |        </values>
            |    </instruction>
            |</instructions>
            |""".stripMargin()

    void setup() {
        nullDirectoryPath = unpackYamlToTempDirectory("null.yml")
        stringDirectoryPath = unpackYamlToTempDirectory("string.yml")
    }

    void cleanup() {
        nullDirectoryPath.toFile().deleteDir()
        stringDirectoryPath.toFile().deleteDir()
    }

    def "should get uppercased null values in SQL for nulls"() {
        when:
        def sql = JFixtures.noConfig().loadDirectory(nullDirectoryPath).compile().toSql99().toString()

        then:
        sql == NULL_EXPECTED_SQL
    }

    def "should not get uppercased values in SQL for strings"() {
        when:
        def sql = JFixtures.noConfig().loadDirectory(stringDirectoryPath).compile().toSql99().toString()

        then:
        sql == STRING_EXPECTED_SQL
    }

    def "should not get uppercased values in XML for nulls"() {
        when:
        def xml = JFixtures.noConfig().loadDirectory(nullDirectoryPath).compile().toXml().toString()

        then:
        xml == NULL_EXPECTED_XML
    }

    def "should not get uppercased values in XML for strings"() {
        when:
        def xml = JFixtures.noConfig().loadDirectory(stringDirectoryPath).compile().toXml().toString()

        then:
        xml == STRING_EXPECTED_XML
    }
}
