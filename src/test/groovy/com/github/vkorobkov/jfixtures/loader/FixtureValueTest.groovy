package com.github.vkorobkov.jfixtures.loader

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class FixtureValueTest extends Specification {

    def "constructor test"() {
        expect:
        FixtureValue.ofAuto(5).value == 5
    }

    def "equals"() {
        expect:
        EqualsVerifier.forClass(FixtureValue).verify()
    }

    def "isString returns true of value has a string type"(value, result) {
        expect:
        FixtureValue.ofAuto(value).string == result

        where:
        value | result
        "str" | true
        'c' as char | false
        true | false
        100 | false
    }

    def "isString returns false when SQL type"() {
        expect:
        !FixtureValue.ofSql("SELECT 1;").isString()
    }

    def "isSql returns true for SQL type"() {
        expect:
        FixtureValue.ofSql("SELECT 1;").isSql()
    }

    def "isSql returns false for auto type"() {
        expect:
        !FixtureValue.ofAuto("vlad").isSql()
    }
}
