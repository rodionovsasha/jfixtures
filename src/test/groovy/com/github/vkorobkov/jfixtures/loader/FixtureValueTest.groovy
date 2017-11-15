package com.github.vkorobkov.jfixtures.loader

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class FixtureValueTest extends Specification {

    def "equals"() {
        expect:
        EqualsVerifier.forClass(FixtureValue).verify()
    }

    def "detects text type without text: prefix"() {
        when:
        def fixture = new FixtureValue("Vlad")

        then:
        fixture.type == ValueType.TEXT
        fixture.value == "Vlad"
    }

    def "detects text type with text: prefix"() {
        when:
        def fixture = new FixtureValue("text:Vlad")

        then:
        fixture.type == ValueType.TEXT
        fixture.value == "Vlad"
    }

    def "does not trim leading spaces for text with prefix"() {
        expect:
        new FixtureValue("text: Vlad").value == " Vlad"
    }

    def "detects sql type with sql: prefix"() {
        when:
        def fixture = new FixtureValue("sql:DEFAULT")

        then:
        fixture.type == ValueType.SQL
        fixture.value == "DEFAULT"
    }

    def "does not trim leading spaces for sql with prefix"() {
        expect:
        new FixtureValue("sql: DEFAULT").value == " DEFAULT"
    }

    def "detects non string types as auto types"() {
        when:
        def fixture = new FixtureValue(value)

        then:
        fixture.type == ValueType.AUTO
        fixture.value == value

        where:
        _ | value
        _ | true
        _ | 50
        _ | 100500L
    }

    def "#getSqlRepresentation returns upper cased null"() {
        expect:
        new FixtureValue(null).getSqlRepresentation() == "NULL"
    }

    def "#getXmlRepresentation returns lower cased null"() {
        expect:
        new FixtureValue(null).getXmlRepresentation() == "null"
    }
}
