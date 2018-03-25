package com.github.vkorobkov.jfixtures.loader

import com.github.vkorobkov.jfixtures.domain.Value
import com.github.vkorobkov.jfixtures.domain.ValueType
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class ValueTest extends Specification {

    def "equals"() {
        expect:
        EqualsVerifier.forClass(Value).verify()
    }

    def "detects text type without text: prefix"() {
        when:
        def fixture = new Value("Vlad")

        then:
        fixture.type == ValueType.TEXT
        fixture.value == "Vlad"
    }

    def "detects text type with text: prefix"() {
        when:
        def fixture = new Value("text:Vlad")

        then:
        fixture.type == ValueType.TEXT
        fixture.value == "Vlad"
    }

    def "does not trim leading spaces for text with prefix"() {
        expect:
        new Value("text: Vlad").value == " Vlad"
    }

    def "detects sql type with sql: prefix"() {
        when:
        def fixture = new Value("sql:DEFAULT")

        then:
        fixture.type == ValueType.SQL
        fixture.value == "DEFAULT"
    }

    def "does not trim leading spaces for sql with prefix"() {
        expect:
        new Value("sql: DEFAULT").value == " DEFAULT"
    }

    def "detects non string types as auto types"() {
        when:
        def fixture = new Value(value)

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
        with(new Value(null)) {
            sqlRepresentation == "NULL"
            type == ValueType.AUTO
        }
    }

    def "#getXmlRepresentation returns lower cased null"() {
        expect:
        with(new Value(null)) {
            xmlRepresentation == "null"
            type == ValueType.AUTO
        }
    }

    def "#getSqlRepresentation returns upper cased boolean"() {
        expect:
        with(new Value(true)) {
            sqlRepresentation == "TRUE"
            type == ValueType.AUTO
        }
    }

    def "#constructor consumes allowed types"() {
        expect:
        new Value(value)

        where:
        _ | value
        _ | null
        _ | true
        _ | Boolean.FALSE
        _ | 5
        _ | Integer.valueOf(5)
        _ | 3.14
        _ | Double.valueOf(3.14)
        _ | "Hello world"
    }

    def "#constructor rejects now allowed type"() {
        when:
        new Value([1, 2, 3])

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Type [class java.util.ArrayList] is not supported by JFixtures at the moment\n" +
            "Read more on https://github.com/vkorobkov/jfixtures/wiki/Type-conversions"
    }
}
