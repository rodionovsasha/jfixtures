package com.github.vkorobkov.jfixtures.loader

import com.github.vkorobkov.jfixtures.domain.Row
import com.github.vkorobkov.jfixtures.domain.Value
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class RowTest extends Specification {
    Map<String, Value> columns

    void setup() {
        columns = [
            id: Value.of(1),
            name: Value.of("Vladimir"),
        ]
    }

    def "constructor test"() {
        when:
        def row = new Row("vlad", columns)

        then:
        row.name == "vlad"
        row.columns == columns
    }

    def "#constuctor may accept either Value or Object as column values"() {
        given:
        def columns = [
            id: Value.of(1),
            name: "Vlad",
            age: 30
        ]

        when:
        def row = new Row("vlad", columns)

        then:
        row.columns == [
            id: Value.of(1),
            name: Value.of("Vlad"),
            age: Value.of(30)
        ]
    }

    def "#columns is a read only collection"() {
        given:
        def row = new Row("vlad", columns)

        when:
        row.columns.remove("id")

        then:
        thrown(UnsupportedOperationException)
    }

    def "#columns adds base columns"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def extendedRow = row.columns(age: 30)

        then:
        extendedRow.columns == columns + [age: Value.of(30)]

        and:
        !extendedRow.is(row)
    }

    def "#columns keeps the original row name"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def extendedRow = row.columns(age: 30)

        then:
        extendedRow.name == "vlad"
    }

    def "#columns overwrites the existing columns"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def extendedRow = row.columns(id: 100)

        then:
        extendedRow.columns == [id: Value.of(100), name: Value.of("Vladimir")]
    }

    def "equals"() {
        expect:
        EqualsVerifier.forClass(Row).verify()
    }

    def "#columns returns row with preserved order from key/value pairs"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def result = row.columns(
                "name", "Vlad",
                "age", 30,
                "hobby", "sleep"
        )

        then:
        with(result.columns) {
            size() == 4
            toMapString() == "[id:Value(value=1, type=AUTO), name:Value(value=Vlad, type=TEXT), age:Value(value=30, type=AUTO), hobby:Value(value=sleep, type=TEXT)]"
        }
    }

    def "#columns throws IllegalArgumentException when odd number of key/value pairs"() {
        given:
        def row = new Row("vlad", columns)

        when:
        row.columns("name", "Vlad", "age")

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Odd number of key/value pairs"
    }

    def "#columns throws IllegalArgumentException when odd object not a string"() {
        given:
        def row = new Row("vlad", columns)

        when:
        row.columns(1, "Vlad", "age", 30)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Key must be a string"
    }
}
