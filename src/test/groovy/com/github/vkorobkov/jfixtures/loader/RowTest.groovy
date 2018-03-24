package com.github.vkorobkov.jfixtures.loader

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class RowTest extends Specification {
    Map<String, Value> columns

    void setup() {
        columns = [
            id: new Value(1),
            name: new Value("Vladimir"),
        ]
    }

    def "constructor test"() {
        when:
        def row = new Row("vlad", columns)

        then:
        row.name == "vlad"
        row.columns == columns
    }

    def "columns is a read only collection"() {
        given:
        def row = new Row("vlad", columns)

        when:
        row.columns.remove("id")

        then:
        thrown(UnsupportedOperationException)
    }

    def "#withBaseColumns adds base columns"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def extendedRow = row.withBaseColumns(age: new Value(30))

        then:
        extendedRow.columns == columns + [age: new Value(30)]

        and:
        !extendedRow.is(row)
    }

    def "#withBaseColumns keeps the original row name"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def extendedRow = row.withBaseColumns(age: new Value(30))

        then:
        extendedRow.name == "vlad"
    }

    def "#withBaseColumns does not overwrite the existing columns"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def extendedRow = row.withBaseColumns(id: new Value(100))

        then:
        extendedRow.columns == columns
    }

    def "#withBaseColumns return the same object(this) base columns is an empty map"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def extendedRow = row.withBaseColumns([:])

        then:
        extendedRow.is(row)
    }

    def "equals"() {
        expect:
        EqualsVerifier.forClass(Row).verify()
    }
}
