package com.github.vkorobkov.jfixtures.fluent.impl

import com.github.vkorobkov.jfixtures.fluent.JFixturesResult
import com.github.vkorobkov.jfixtures.sql.Appender
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import javax.xml.bind.JAXBException
import java.nio.file.Path

class XmlJFixturesResultImplTest extends Specification implements YamlVirtualFolder {
    Path tmlFolderPath
    String outputPath
    Appender appender
    JFixturesResult fixturesResult

    def EXPECTED_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            |<instructions>
            |    <instruction type="CleanTable" table="users" cleanMethod="DELETE"/>
            |    <instruction type="CustomSql">// Doing table users</instruction>
            |    <instruction type="CustomSql">BEGIN TRANSACTION;</instruction>
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
        appender = new StringAppender()

        tmlFolderPath = unpackYamlToTempFolder("default.yml")
        outputPath = tmlFolderPath.resolve("out.xml") as String

        fixturesResult = new XmlJFixturesResultImpl(tmlFolderPath as String)
    }

    void cleanup() {
        tmlFolderPath.toFile().deleteDir()
    }

    def "default constructor"() {
        expect:
        new XmlJFixturesResultImpl()
    }

    def "processes fixtures to string"() {
        expect:
        fixturesResult.asString() == EXPECTED_XML
    }

    def "second transformation to string returns the same result"() {
        expect:
        fixturesResult.asString() == EXPECTED_XML

        and:
        fixturesResult.asString() == EXPECTED_XML
    }

    def "saves result to file"() {
        when:
        fixturesResult.toFile(outputPath)

        then:
        new File(outputPath).text == EXPECTED_XML
    }

    def "save to file overrides previous file"() {
        given:
        fixturesResult.toFile(outputPath)

        when:
        fixturesResult.toFile(outputPath)

        then:
        new File(outputPath).text == EXPECTED_XML
    }

    def "throws JAXBException on empty file path"() {
        when:
        fixturesResult.toFile("")

        then:
        thrown(JAXBException)
    }
}
