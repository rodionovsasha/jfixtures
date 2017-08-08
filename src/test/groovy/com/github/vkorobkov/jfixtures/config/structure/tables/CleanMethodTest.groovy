package com.github.vkorobkov.jfixtures.config.structure.tables

import spock.lang.Specification

class CleanMethodTest extends Specification {
    def "CleanMethod positive cases"(String value, expected) {
        expect:
        CleanMethod.valueOfIgnoreCase(value) == expected

        where:
        value    | expected
        "delete" | CleanMethod.DELETE
        "Delete" | CleanMethod.DELETE
        "DELETE" | CleanMethod.DELETE
        "none"   | CleanMethod.NONE
        "NONE"   | CleanMethod.NONE
    }

    def "CleanMethod throws exception when wrong value is provided"() {
        when:
        CleanMethod.valueOf("wrong_method")

        then:
        thrown(IllegalArgumentException)
    }
}
