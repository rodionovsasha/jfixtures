package com.github.vkorobkov.jfixtures.instructions

import spock.lang.Specification

class CleanTableTest extends Specification {
    def "constructor test"() {
        expect:
        new CleanTable("users").table == "users"
    }

    def "visitor accepts instruction"() {
        given:
        def visitor = Mock(InstructionVisitor)
        def instruction = new CleanTable("users")

        when:
        instruction.accept(visitor)

        then:
        1 * visitor.visit(instruction)
    }
}
