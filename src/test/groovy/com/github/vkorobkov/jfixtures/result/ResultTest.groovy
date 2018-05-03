package com.github.vkorobkov.jfixtures.result

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod
import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.instructions.InstructionVisitor
import spock.lang.Specification

class ResultTest extends Specification {

    def instructions = [
        new CleanTable("users", CleanMethod.DELETE),
        new InsertRow("users", "vlad", [id: 1, name: "Vlad", age: 30])
    ]

    def "::constructor saves passed in instructions"() {
        expect:
        new Result(instructions).instructions.toList() == instructions
    }

    def "::constructor wraps instructions with unmodifiable collection"() {
        when:
        new Result(instructions).instructions.clear()

        then:
        thrown(UnsupportedOperationException)
    }

    def "::visit calls visitor for every instruction"() {
        given:
        def visitor = Mock(InstructionVisitor)

        when:
        new Result(instructions).visit(visitor)

        then:
        instructions.each {
            1 * visitor.visit(it)
        }
    }
}
