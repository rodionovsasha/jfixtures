package com.github.vkorobkov.jfixtures.loader

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class FixtureValueTest extends Specification {

    def "constructor test"() {
        expect:
        new FixtureValue(5).value == 5
    }

    def "equals"() {
        expect:
        EqualsVerifier.forClass(FixtureValue).verify()
    }

    def "isString returns true of value has a string type"(value, result) {
        expect:
        new FixtureValue(value).string == result

        where:
        value | result
        "str" | true
        'c' as char | false
        true | false
        100 | false
    }
}
