package com.github.vkorobkov.jfixtures.instructions

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod
import spock.lang.Specification

class CleanTableTest extends Specification {
    def "constructor test"() {
        expect:
        new CleanTable("users", CleanMethod.DELETE).table == "users"
    }

    def "visitor accepts instruction"() {
        given:
        def visitor = Mock(InstructionVisitor)
        def instruction = new CleanTable("users", CleanMethod.DELETE)

        when:
        instruction.accept(visitor)

        then:
        1 * visitor.visit(instruction)
    }
}
