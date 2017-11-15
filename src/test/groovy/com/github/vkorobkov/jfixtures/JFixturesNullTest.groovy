package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class JFixturesNullTest extends Specification implements YamlVirtualFolder {
    Path nullFolderPath
    Path stringFolderPath

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
        nullFolderPath = unpackYamlToTempFolder("null.yml")
        stringFolderPath = unpackYamlToTempFolder("string.yml")
    }

    void cleanup() {
        nullFolderPath.toFile().deleteDir()
        stringFolderPath.toFile().deleteDir()
    }

    def "should get uppercased null values in SQL for nulls"() {
        expect:
        JFixtures.sql99(nullFolderPath as String).asString() == NULL_EXPECTED_SQL
    }

    def "should not get uppercased values in SQL for strings"() {
        expect:
        JFixtures.sql99(stringFolderPath as String).asString() == STRING_EXPECTED_SQL
    }

    def "should not get uppercased values in XML for nulls"() {
        expect:
        JFixtures.xml(nullFolderPath as String).asString() == NULL_EXPECTED_XML
    }

    def "should not get uppercased values in XML for strings"() {
        expect:
        JFixtures.xml(stringFolderPath as String).asString() == STRING_EXPECTED_XML
    }
}
