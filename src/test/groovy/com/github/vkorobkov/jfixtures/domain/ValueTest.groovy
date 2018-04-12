package com.github.vkorobkov.jfixtures.domain

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class ValueTest extends Specification {

    def "equals"() {
        expect:
        EqualsVerifier.forClass(Value).verify()
    }

    def "detects text type without text: prefix"() {
        when:
        def fixture = Value.of("Vlad")

        then:
        fixture.type == ValueType.TEXT
        fixture.value == "Vlad"
    }

    def "detects text type with text: prefix"() {
        when:
        def fixture = Value.ofText("Vlad")

        then:
        fixture.type == ValueType.TEXT
        fixture.value == "Vlad"
    }

    def "does not trim leading spaces for text with prefix"() {
        expect:
        Value.ofText(" Vlad").value == " Vlad"
    }

    def "detects sql type with sql: prefix"() {
        when:
        def fixture = Value.ofSql("DEFAULT")

        then:
        fixture.type == ValueType.SQL
        fixture.value == "DEFAULT"
    }

    def "does not trim leading spaces for sql with prefix"() {
        expect:
        Value.ofSql(" DEFAULT").value == " DEFAULT"
    }

    def "detects non string types as auto types"() {
        when:
        def fixture = Value.of(value)

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
        with(Value.ofNull()) {
            sqlRepresentation == "NULL"
            type == ValueType.AUTO
        }
    }

    def "#getXmlRepresentation returns lower cased null"() {
        expect:
        with(Value.ofNull()) {
            xmlRepresentation == "null"
            type == ValueType.AUTO
        }
    }

    def "#getSqlRepresentation returns upper cased boolean"() {
        expect:
        with(Value.of(true)) {
            sqlRepresentation == "TRUE"
            type == ValueType.AUTO
        }
    }

    def "#constructor consumes allowed types"() {
        expect:
        Value.of(value)

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
        Value.of([1, 2, 3])

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Type [class java.util.ArrayList] is not supported by JFixtures at the moment\n" +
            "Read more on https://github.com/vkorobkov/jfixtures/wiki/Type-conversions"
    }

    def "#of instantiates a Value.of object"() {
        expect:
        with(Value.of(true)) {
            sqlRepresentation == "TRUE"
            type == ValueType.AUTO
        }
    }

    def "#of broadcasts Value as it is"() {
        given:
        Value source = Value.of(true)

        when:
        Value wrapped = Value.of(source)

        then:
        wrapped.is(source)
    }

    def "#ofSql instantiates a Value.of object from Sql string"() {
        expect:
        with(Value.ofSql("select 1")) {
            sqlRepresentation == "select 1"
            type == ValueType.SQL
        }
    }

    def "#ofText instantiates a Value.of object from text"() {
        expect:
        with(Value.ofText("select 1")) {
            sqlRepresentation == "select 1"
            type == ValueType.TEXT
        }
    }

    def "#ofNull instantiates a Value.of object from null"() {
        expect:
        with(Value.ofNull()) {
            sqlRepresentation == "NULL"
            type == ValueType.AUTO
        }
    }
}
