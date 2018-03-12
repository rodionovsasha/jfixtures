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
}
