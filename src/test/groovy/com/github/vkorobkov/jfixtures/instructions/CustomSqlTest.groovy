package com.github.vkorobkov.jfixtures.instructions

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class CustomSqlTest extends Specification {
    def "default constructor test"() {
        expect:
        new CustomSql()
    }

    def "constructor test"() {
        when:
        CustomSql customSql = new CustomSql("users", "BEGIN TRANSACTION;")

        then:
        customSql.instruction == "BEGIN TRANSACTION;"
    }

    def "should replace table name placeholder"() {
        when:
        CustomSql customSql = new CustomSql("users", "// Doing table \$TABLE_NAME")

        then:
        customSql.instruction == "// Doing table users"
    }

    def "visitor accepts instruction"() {
        given:
        def visitor = Mock(InstructionVisitor)
        def instruction = new CustomSql("users", "BEGIN TRANSACTION;")

        when:
        instruction.accept(visitor)

        then:
        1 * visitor.visit(instruction)
    }

    def "::equals"() {
        expect:
        EqualsVerifier
            .forClass(CustomSql)
            .suppress(Warning.STRICT_INHERITANCE, Warning.NONFINAL_FIELDS)
            .verify()
    }
}
