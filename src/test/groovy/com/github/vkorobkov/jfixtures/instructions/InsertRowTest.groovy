package com.github.vkorobkov.jfixtures.instructions

import com.github.vkorobkov.jfixtures.loader.FixtureValue
import spock.lang.Specification

class InsertRowTest extends Specification {

    def "constructor test"() {
        given:
        def values = [
            "id": FixtureValue.ofAuto(5),
            "name": FixtureValue.ofAuto("Vlad")
        ]

        when:
        def row = new InsertRow("users", "vlad", values)

        then:
        row.table == "users"
        row.rowName == "vlad"

        and:
        row.values.id.value == 5
        row.values.name.value == "Vlad"
    }

    def "values collection is immutable"() {
        given:
        def values = [
            "id": FixtureValue.ofAuto(5),
            "name": FixtureValue.ofAuto("Vlad")
        ]

        when:
        def row = new InsertRow("users", "vlad", values)
        row.values.remove("id")

        then:
        thrown(UnsupportedOperationException)
    }

    def "value toString() dummy test"() {
        expect:
        FixtureValue.ofAuto("5").toString() == FixtureValue.ofAuto("5").toString()
    }

    def "visitor accepts instruction"() {
        given:
        def visitor = Mock(InstructionVisitor)
        def instruction = new InsertRow("users", "vlad", [:])

        when:
        instruction.accept(visitor)

        then:
        1 * visitor.visit(instruction)
    }
}
