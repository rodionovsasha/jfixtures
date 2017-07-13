package com.github.vkorobkov.jfixtures.loader

import spock.lang.Specification

class ValueTypeTest extends Specification {

    def "valueOfIgnoreCase positive cases"(str, expected) {
        expect:
        ValueType.valueOfIgnoreCase(str) == expected

        where:
        str | expected
        "auto" | ValueType.AUTO
        "AUTO" | ValueType.AUTO
        "Auto" | ValueType.AUTO
        "Sql" | ValueType.SQL
        "sql" | ValueType.SQL
        "SQL" | ValueType.SQL
    }

    def "valueOfIgnoreCase throws exception when wrong value is provided"() {
        when:
        ValueType.valueOfIgnoreCase("Unicorn")

        then:
        thrown(IllegalArgumentException)
    }
}
