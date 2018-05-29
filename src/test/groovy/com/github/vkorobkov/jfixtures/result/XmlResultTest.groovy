package com.github.vkorobkov.jfixtures.result

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod
import com.github.vkorobkov.jfixtures.domain.Value
import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.testutil.Assertions
import spock.lang.FailsWith
import spock.lang.Shared
import spock.lang.Specification

import javax.xml.bind.JAXBException
import java.nio.file.Files

class XmlResultTest extends Specification implements Assertions {
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
    def subject = new XmlResult(instructions)

    def EXPECTED_XML = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
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
            |                <value type="AUTO">30</value>
            |            </entry>
            |        </values>
            |    </instruction>
            |</instructions>
            |""".stripMargin()

    def "::constructor(Collection<Instruction>) saves instructions"() {
        expect:
        assertCollectionsEqual(subject.instructions, instructions)
    }

    def "::constructor(Collection<Instruction>) saves instructions as unmodifiable collection"() {
        expect:
        assertUnmodifiableCollection(subject.instructions)
    }

    def "::constructor() leaves instructions being null"() {
        expect:
        new XmlResult().instructions == null
    }

    def "::toString returns string representation of output XML"() {
        expect:
        subject.toString() == EXPECTED_XML
    }

    def "::toFile writes output XML to file"() {
        setup:
        def file = createTempOutputFile()

        when:
        subject.toFile(file.toString())

        then:
        file.text == EXPECTED_XML

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
        file.text == EXPECTED_XML

        cleanup:
        file.toFile().delete()
    }

    @FailsWith(JAXBException)
    def "::toFile does not shallow underlying exceptions"() {
        expect:
        subject.toFile("")
    }

    private static createTempOutputFile() {
        Files.createTempFile("jfixtures", "output.xml")
    }
}
