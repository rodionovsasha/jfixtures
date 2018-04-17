package com.github.vkorobkov.jfixtures.domain

import spock.lang.Specification

class TableTest extends Specification {
    Collection<Row> rows

    void setup() {
        rows = [
                Row.ofName("Vlad"),
                Row.ofName("Bob"),
                Row.ofName("Ned"),
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
            [Row.of("Vlad", [age: 30]), Row.of("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
                Row.of("Homer", [age: 50]),
                Row.of("Bart", [age: 12])
        ])

        then:
        merged.rows.asList() == [
                Row.of("Vlad", [age: 30]),
                Row.of("Mr Burns", [age: 100]),
                Row.of("Homer", [age: 50]),
                Row.of("Bart", [age: 12])
        ]
    }

    def "#mergeRows replaces rows with the same name"() {
        given:
        def fixture = new Table("users",
            [Row.of("Vlad", [age: 29]), Row.of("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
                Row.of("Homer", [age: 50]),
                Row.of("Vlad", [age: 30, skill: "java"])
        ])

        then:
        merged.rows.asList() == [
                Row.of("Vlad", [age: 30, skill: "java"]),
                Row.of("Mr Burns", [age: 100]),
                Row.of("Homer", [age: 50])
        ]
    }

    def "#mergeRows returns original rows if rows to merge is an empty list"() {
        given:
        def fixture = new Table("users",
            [Row.of("Vlad", [age: 30]), Row.of("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([])

        then:
        merged.rows.asList() == [
                Row.of("Vlad", [age: 30]),
                Row.of("Mr Burns", [age: 100])
        ]
    }

    def "#mergeRows returns fixture with original name"() {
        given:
        def fixture = new Table("users",
            [Row.of("Vlad", [age: 29]), Row.of("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
                Row.of("Homer", [age: 50]),
                Row.of("Vlad", [age: 30, skill: "java"])
        ])

        then:
        merged.name == "users"
    }

    def "#mergeTables puts fixtures for different tables in sequence"() {
        given:
        def users = new Table("users", [
                Row.of("Vlad",  [name: "Vlad", age: 30]),
                Row.of("Burns", [name: "Mr Burns", age: 130])
        ])
        def roles = new Table("roles", [
                Row.of("user",  [type: "user", readAccess: true, writeAccess: false]),
                Row.of("admin", [type: "admin", readAccess: true, writeAccess: true])
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
                Row.of("Vlad",  [name: "Vlad", age: 30]),
                Row.of("Burns", [name: "Mr Burns", age: 130])
        ])
        def users2 = new Table("users", [
                Row.of("Homer", [name: "Homer", age: 40]),
                Row.of("Bart", [name: "Bart", age: 12])
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
                Row.of("Vlad",  [name: "Vlad", age: 29]),
                Row.of("Burns", [name: "Mr Burns", age: 130])
        ])
        def users2 = new Table("users", [
                Row.of("Vlad", [name: "Vladimir", age: 30])
        ])

        when:
        def merged = Table.mergeTables([users1, users2])

        then:
        merged.size() == 1

        and:
        with(merged[0]) {
            assert name == "users"
            assert rows.toList() == [
                    Row.of("Vlad", [name: "Vladimir", age: 30]), Row.of("Burns", [name: "Mr Burns", age: 130])
            ]
        }

    }
}
