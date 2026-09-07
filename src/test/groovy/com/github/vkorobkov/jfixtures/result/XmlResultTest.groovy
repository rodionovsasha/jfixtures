package com.github.rodionovsasha.jfixtures.result

import com.github.rodionovsasha.jfixtures.config.structure.tables.CleanMethod
import com.github.rodionovsasha.jfixtures.domain.Value
import com.github.rodionovsasha.jfixtures.instructions.CleanTable
import com.github.rodionovsasha.jfixtures.instructions.InsertRow
import com.github.rodionovsasha.jfixtures.testutil.Assertions
import spock.lang.FailsWith
import spock.lang.Shared
import spock.lang.Specification

import jakarta.xml.bind.JAXBException
import jakarta.xml.bind.annotation.XmlElement
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

    def "::toString propagates JAXB context creation errors"() {
        when:
        new InvalidXmlResult().toString()

        then:
        thrown(JAXBException)
    }

    def "::toFile propagates JAXB context creation errors without creating a file"() {
        given:
        def directory = Files.createTempDirectory("jfixtures-invalid-xml")
        def output = directory.resolve("output.xml")

        when:
        new InvalidXmlResult().toFile(output.toString())

        then:
        thrown(JAXBException)
        !Files.exists(output)

        cleanup:
        Files.deleteIfExists(output)
        Files.deleteIfExists(directory)
    }

    // JAXB cannot bind an interface; subclasses with invalid mappings must fail visibly.
    static class InvalidXmlResult extends XmlResult {
        @XmlElement
        public Runnable unsupported
    }

    private static createTempOutputFile() {
        Files.createTempFile("jfixtures", "output.xml")
    }
}
