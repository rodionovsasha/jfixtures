package com.github.vkorobkov.jfixtures.loader

import spock.lang.Specification

class FixtureTest extends Specification {
    Collection<FixtureRow> rows

    void setup() {
        rows = [
            new FixtureRow("Vlad", [:]),
            new FixtureRow("Bob", [:]),
            new FixtureRow("Ned", [:]),
        ]
    }

    def "dummy constructor test"() {
        when:
        def fixture = new Fixture("users", [])

        then:
        fixture.name == "users"
        fixture.rows.empty
    }

    def "returns rows provided by constructor"() {
        when:
        def fixture = new Fixture("users", rows)

        then:
        fixture.rows.asList() == rows
    }

    def "rows is a read only collection"() {
        when:
        def fixture = new Fixture("users", rows)
        fixture.rows.remove("Vlad")

        then:
        thrown(UnsupportedOperationException)
    }

    def "rows returns the same collection every call"() {
        when:
        def fixture = new Fixture("users", rows)

        then:
        fixture.name == "users"
        fixture.rows.is(fixture.rows)
    }

    def "#mergeRows adds new rows to the end"() {
        given:
        def fixture = new Fixture("users",
            [new FixtureRow("Vlad", [age: 30]), new FixtureRow("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
            new FixtureRow("Homer", [age: 50]),
            new FixtureRow("Bart", [age: 12])
        ])

        then:
        merged.rows.asList() == [
            new FixtureRow("Vlad", [age: 30]),
            new FixtureRow("Mr Burns", [age: 100]),
            new FixtureRow("Homer", [age: 50]),
            new FixtureRow("Bart", [age: 12])
        ]
    }

    def "#mergeRows replaces rows with the same name"() {
        given:
        def fixture = new Fixture("users",
            [new FixtureRow("Vlad", [age: 29]), new FixtureRow("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
            new FixtureRow("Homer", [age: 50]),
            new FixtureRow("Vlad", [age: 30, skill: "java"])
        ])

        then:
        merged.rows.asList() == [
            new FixtureRow("Vlad", [age: 30, skill: "java"]),
            new FixtureRow("Mr Burns", [age: 100]),
            new FixtureRow("Homer", [age: 50])
        ]
    }

    def "#mergeRows returns original rows if rows to merge is an empty list"() {
        given:
        def fixture = new Fixture("users",
            [new FixtureRow("Vlad", [age: 30]), new FixtureRow("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([])

        then:
        merged.rows.asList() == [
            new FixtureRow("Vlad", [age: 30]),
            new FixtureRow("Mr Burns", [age: 100])
        ]
    }

    def "#mergeRows returns fixture with original name"() {
        given:
        def fixture = new Fixture("users",
            [new FixtureRow("Vlad", [age: 29]), new FixtureRow("Mr Burns", [age: 100])]
        )

        when:
        def merged = fixture.mergeRows([
            new FixtureRow("Homer", [age: 50]),
            new FixtureRow("Vlad", [age: 30, skill: "java"])
        ])

        then:
        merged.name == "users"
    }

    def "#mergeFixtures puts fixtures for different tables in sequence"() {
        given:
        def users = new Fixture("users", [
            new FixtureRow("Vlad",  [name: "Vlad", age: 30]),
            new FixtureRow("Burns", [name: "Mr Burns", age: 130])
        ])
        def roles = new Fixture("roles", [
            new FixtureRow("user",  [type: "user", readAccess: true, writeAccess: false]),
            new FixtureRow("admin", [type: "admin", readAccess: true, writeAccess: true])
        ])

        when:
        def merged = Fixture.mergeFixtures([users, roles])

        then:
        merged.size() == 2

        and:
        merged[0] == users
        merged[1] == roles
    }

    def "#mergeFixtures concatenates different rows of the same table"() {
        given:
        def users1 = new Fixture("users", [
            new FixtureRow("Vlad",  [name: "Vlad", age: 30]),
            new FixtureRow("Burns", [name: "Mr Burns", age: 130])
        ])
        def users2 = new Fixture("users", [
            new FixtureRow("Homer", [name: "Homer", age: 40]),
            new FixtureRow("Bart", [name: "Bart", age: 12])
        ])

        when:
        def merged = Fixture.mergeFixtures([users1, users2])

        then:
        merged.size() == 1

        and:
        with(merged[0]) {
            assert name == "users"
            assert rows.toList() == users1.rows + users2.rows
        }
    }

    def "#mergeFixtures replaces old rows with new ones in scope of the same table"() {
        given:
        def users1 = new Fixture("users", [
            new FixtureRow("Vlad",  [name: "Vlad", age: 29]),
            new FixtureRow("Burns", [name: "Mr Burns", age: 130])
        ])
        def users2 = new Fixture("users", [
            new FixtureRow("Vlad", [name: "Vladimir", age: 30])
        ])

        when:
        def merged = Fixture.mergeFixtures([users1, users2])

        then:
        merged.size() == 1

        and:
        with(merged[0]) {
            assert name == "users"
            assert rows.toList() == [
                new FixtureRow("Vlad", [name: "Vladimir", age: 30]), new FixtureRow("Burns", [name: "Mr Burns", age: 130])
            ]
        }

    }
}
