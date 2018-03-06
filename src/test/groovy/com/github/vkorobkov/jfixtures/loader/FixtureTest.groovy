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
}
