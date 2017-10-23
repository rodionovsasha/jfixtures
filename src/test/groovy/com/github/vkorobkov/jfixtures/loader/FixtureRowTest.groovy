package com.github.vkorobkov.jfixtures.loader

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class FixtureRowTest extends Specification {
    Map<String, FixtureValue> columns

    void setup() {
        columns = [
                id  : new FixtureValue(1),
                name: new FixtureValue("Vladimir"),
        ]
    }

    def "constructor test"() {
        when:
        def row = new FixtureRow("vlad", columns)

        then:
        row.name == "vlad"
        row.columns == columns
    }

    def "columns is a read only collection"() {
        given:
        def row = new FixtureRow("vlad", columns)

        when:
        row.columns.remove("id")

        then:
        thrown(UnsupportedOperationException)
    }

    def "equals"() {
        expect:
        EqualsVerifier.forClass(FixtureRow).verify()
    }
}
