package com.github.vkorobkov.jfixtures.loader

import spock.lang.Specification

import java.util.function.Supplier

class FixtureTest extends Specification {
    Collection<FixtureRow> userRows
    RowsSupplier rowsSupplier

    void setup() {
        rowsSupplier = new RowsSupplier()
        userRows = [
                new FixtureRow("Vlad", [:]),
                new FixtureRow("Bob", [:]),
                new FixtureRow("Ned", [:]),
        ]
    }

    def "dummy constructor test"() {
        given:
        rowsSupplier.rows = []

        when:
        def fixture = new Fixture("users", rowsSupplier)

        then:
        fixture.name == "users"
        fixture.rows.empty
    }

    def "returns rows provided by supplier"() {
        given:
        rowsSupplier.rows = userRows

        when:
        def fixture = new Fixture("users", rowsSupplier)

        then:
        fixture.rows.asList() == userRows
    }

    def "rows is a read only collection"() {
        given:
        rowsSupplier.rows = userRows

        when:
        def fixture = new Fixture("users", rowsSupplier)
        fixture.rows.remove("Vlad")

        then:
        thrown(UnsupportedOperationException)
    }


    def "rows returns the same collection every call"() {
        given:
        rowsSupplier.rows = userRows

        when:
        def fixture = new Fixture("users", rowsSupplier)

        then:
        fixture.name == "users"
        fixture.rows.is(fixture.rows)
    }

    def "rows is a lazy loaded field"() {
        given:
        rowsSupplier.rows = userRows

        when:
        def fixture = new Fixture("users", rowsSupplier)

        then:
        rowsSupplier.invocationCount == 0

        and:
        fixture.rows.size() == userRows.size()
        rowsSupplier.invocationCount == 1

        and:
        fixture.rows.size() == userRows.size()
        rowsSupplier.invocationCount == 1
    }

    class RowsSupplier implements Supplier<Collection<FixtureRow>> {
        Collection<FixtureRow> rows
        int invocationCount = 0

        @Override
        Collection<FixtureRow> get() {
            ++invocationCount
            rows
        }
    }
}
