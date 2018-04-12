package com.github.vkorobkov.jfixtures.domain

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

    def "#columns(Object...) returns row from key/value pairs"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def result = row.columns(
                "name", "Vlad",
                "age", 30,
                "hobby", "sleep"
        )

        then:
        result.columns.toMapString() == [id: Value.of(1), name: Value.of("Vlad"), age: Value.of(30), hobby: Value.of("sleep")].toMapString()
    }

    def "#columns(Object...) throws IllegalArgumentException when odd number of key/value pairs"() {
        given:
        def row = new Row("vlad", columns)

        when:
        row.columns("name", "Vlad", "age")

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Parameter <keyValuePairs> is expected to have odd length since it represents key/value pairs"
    }

    def "#columns(Object...) throws IllegalArgumentException when odd object is not a string"() {
        given:
        def row = new Row("vlad", columns)

        when:
        row.columns(1, "Vlad", "age", 30)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Column name is expected to be a string, but was passed class = [class java.lang.Integer], value = [1]"
    }

    def "#columns(Object...) returns original row when an empty array passed"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def result = row.columns([] as Object[])

        then:
        result.columns.toMapString() == [id: Value.of(1), name: Value.of("Vladimir")].toMapString()
    }

    def "#columns(Object...) throws IllegalArgumentException when name is null"() {
        given:
        def row = new Row("vlad", columns)

        when:
        row.columns(null, "Vlad")

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Column name is expected to be a string, but was passed null"
    }

    def "#columns(Object...) throws IllegalArgumentException when single string passed"() {
        given:
        def row = new Row("vlad", columns)

        when:
        row.columns("Vlad")

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Parameter <keyValuePairs> is expected to have odd length since it represents key/value pairs"
    }

    def "#column adds a new column to the row"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def result = row.column("age", 30)

        then:
        result.columns.toMapString() == [id: Value.of(1), name: Value.of("Vladimir"), age: Value.of(30)].toMapString()
    }

    def "#nullColumn adds a new column to the row"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def result = row.nullColumn("age")

        then:
        result.columns.toMapString() == [id: Value.of(1), name: Value.of("Vladimir"), age: Value.ofNull()].toMapString()
    }

    def "#sqlColumn adds a new column to the row"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def result = row.sqlColumn("age", "SELECT 1")

        then:
        result.columns.toMapString() == [id: Value.of(1), name: Value.of("Vladimir"), age: Value.ofSql("SELECT 1")].toMapString()
    }

    def "#textColumn adds a new column to the row"() {
        given:
        def row = new Row("vlad", columns)

        when:
        def result = row.textColumn("age", "SELECT 1")

        then:
        result.columns.toMapString() == [id: Value.of(1), name: Value.of("Vladimir"), age: Value.ofText("SELECT 1")].toMapString()
    }
}
