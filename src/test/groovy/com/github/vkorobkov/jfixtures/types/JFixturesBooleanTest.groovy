package com.github.vkorobkov.jfixtures.types

import com.github.vkorobkov.jfixtures.JFixtures
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class JFixturesBooleanTest extends Specification implements YamlVirtualFolder {
    Path folderPath

    def EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "active1", "active2", "answer1", "answer2", "logical1", "logical2", "option1", "option2") VALUES (1, 'homer', TRUE, FALSE, FALSE, TRUE, TRUE, FALSE, TRUE, FALSE);
            |""".stripMargin()
    def EXPECTED_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
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
            |                <value type="TEXT">homer</value>
            |            </entry>
            |            <entry>
            |                <key>active1</key>
            |                <value type="AUTO">true</value>
            |            </entry>
            |            <entry>
            |                <key>active2</key>
            |                <value type="AUTO">false</value>
            |            </entry>
            |            <entry>
            |                <key>answer1</key>
            |                <value type="AUTO">false</value>
            |            </entry>
            |            <entry>
            |                <key>answer2</key>
            |                <value type="AUTO">true</value>
            |            </entry>
            |            <entry>
            |                <key>logical1</key>
            |                <value type="AUTO">true</value>
            |            </entry>
            |            <entry>
            |                <key>logical2</key>
            |                <value type="AUTO">false</value>
            |            </entry>
            |            <entry>
            |                <key>option1</key>
            |                <value type="AUTO">true</value>
            |            </entry>
            |            <entry>
            |                <key>option2</key>
            |                <value type="AUTO">false</value>
            |            </entry>
            |        </values>
            |    </instruction>
            |</instructions>
            |""".stripMargin()

    void setup() {
        folderPath = unpackYamlToTempFolder("boolean.yml")
    }

    void cleanup() {
        folderPath.toFile().deleteDir()
    }

    def "should get uppercased null values in SQL for nulls"() {
        expect:
        JFixtures.sql99(folderPath as String).asString() == EXPECTED_SQL
    }

    def "should not get uppercased boolean values in XML"() {
        expect:
        JFixtures.xml(folderPath as String).asString() == EXPECTED_XML
    }
}
