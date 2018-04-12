package com.github.vkorobkov.jfixtures.domain

import spock.lang.Specification

class TableTest extends Specification {
    Collection<Row> rows

    void setup() {
        rows = [
                new Row("Vlad", [:]),
                new Row("Bob", [:]),
                new Row("Ned", [:]),
        ]
    }

    def "dummy constructor test"() {
        when:
        def fixture = new Table("users", [])

        then:
        fixture.name == "users"
        fixture.rows.empty
    }

    def "returns rows provided by constructor"() {
        when:
        def fixture = new Table("users", rows)

        then:
        fixture.rows.asList() == rows
    }

    def "rows is a read only collection"() {
        when:
        def fixture = new Table("users", rows)
        fixture.rows.remove("Vlad")

        then:
        thrown(UnsupportedOperationException)
    }

    def "rows returns the same collection every call"() {
        when:
        def fixture = new Table("users", rows)

        then:
        fixture.name == "users"
        fixture.rows.is(fixture.rows)
    }

    def "#mergeRows adds new rows to the end"() {
        given:
        def fixture = new Table("users",
            [new Row("Vlad", [age: 30]), new Row("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
                new Row("Homer", [age: 50]),
                new Row("Bart", [age: 12])
        ])

        then:
        merged.rows.asList() == [
                new Row("Vlad", [age: 30]),
                new Row("Mr Burns", [age: 100]),
                new Row("Homer", [age: 50]),
                new Row("Bart", [age: 12])
        ]
    }

    def "#mergeRows replaces rows with the same name"() {
        given:
        def fixture = new Table("users",
            [new Row("Vlad", [age: 29]), new Row("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
                new Row("Homer", [age: 50]),
                new Row("Vlad", [age: 30, skill: "java"])
        ])

        then:
        merged.rows.asList() == [
                new Row("Vlad", [age: 30, skill: "java"]),
                new Row("Mr Burns", [age: 100]),
                new Row("Homer", [age: 50])
        ]
    }

    def "#mergeRows returns original rows if rows to merge is an empty list"() {
        given:
        def fixture = new Table("users",
            [new Row("Vlad", [age: 30]), new Row("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([])

        then:
        merged.rows.asList() == [
                new Row("Vlad", [age: 30]),
                new Row("Mr Burns", [age: 100])
        ]
    }

    def "#mergeRows returns fixture with original name"() {
        given:
        def fixture = new Table("users",
            [new Row("Vlad", [age: 29]), new Row("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
                new Row("Homer", [age: 50]),
                new Row("Vlad", [age: 30, skill: "java"])
        ])

        then:
        merged.name == "users"
    }

    def "#mergeTables puts fixtures for different tables in sequence"() {
        given:
        def users = new Table("users", [
                new Row("Vlad",  [name: "Vlad", age: 30]),
                new Row("Burns", [name: "Mr Burns", age: 130])
        ])
        def roles = new Table("roles", [
                new Row("user",  [type: "user", readAccess: true, writeAccess: false]),
                new Row("admin", [type: "admin", readAccess: true, writeAccess: true])
        ])

        when:
        def merged = Table.mergeTables([users, roles])

        then:
        merged.size() == 2

        and:
        merged[0] == users
        merged[1] == roles
    }

    def "#mergeTables concatenates different rows of the same table"() {
        given:
        def users1 = new Table("users", [
                new Row("Vlad",  [name: "Vlad", age: 30]),
                new Row("Burns", [name: "Mr Burns", age: 130])
        ])
        def users2 = new Table("users", [
                new Row("Homer", [name: "Homer", age: 40]),
                new Row("Bart", [name: "Bart", age: 12])
        ])

        when:
        def merged = Table.mergeTables([users1, users2])

        then:
        merged.size() == 1

        and:
        with(merged[0]) {
            assert name == "users"
            assert rows.toList() == users1.rows + users2.rows
        }
    }

    def "#mergeTables replaces old rows with new ones in scope of the same table"() {
        given:
        def users1 = new Table("users", [
                new Row("Vlad",  [name: "Vlad", age: 29]),
                new Row("Burns", [name: "Mr Burns", age: 130])
        ])
        def users2 = new Table("users", [
            new Row("Vlad", [name: "Vladimir", age: 30])
        ])

        when:
        def merged = Table.mergeTables([users1, users2])

        then:
        merged.size() == 1

        and:
        with(merged[0]) {
            assert name == "users"
            assert rows.toList() == [
                    new Row("Vlad", [name: "Vladimir", age: 30]), new Row("Burns", [name: "Mr Burns", age: 130])
            ]
        }

    }
}
