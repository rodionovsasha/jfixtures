package com.github.vkorobkov.jfixtures.result

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod
import com.github.vkorobkov.jfixtures.domain.Value
import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.instructions.InstructionVisitor
import com.github.vkorobkov.jfixtures.sql.Sql
import com.github.vkorobkov.jfixtures.sql.SqlType
import com.github.vkorobkov.jfixtures.testutil.Assertions
import spock.lang.Specification
import spock.lang.Unroll

class ResultTest extends Specification implements Assertions {

    def instructions = [
        new CleanTable("users", CleanMethod.DELETE),
        new InsertRow("users", "vlad", [
                id: Value.of(1),
                name:  Value.of("Vlad"),
                age: Value.of( 30)
        ])
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

    def "::toSql99 returns SqlResult with instructions and SQL99 dialect"() {
        when:
        def result = new Result(instructions).toSql99()

        then:
        assertCollectionsEqual(result.instructions, instructions)
        result.sql == SqlType.SQL99.sqlDialect
    }

    def "::toMySql returns SqlResult with instructions and MySql dialect"() {
        when:
        def result = new Result(instructions).toMySql()

        then:
        assertCollectionsEqual(result.instructions, instructions)
        result.sql == SqlType.MYSQL.sqlDialect
    }

    def "::toMicrosoftSql returns SqlResult with instructions and Microsoft Sql dialect"() {
        when:
        def result = new Result(instructions).toMicrosoftSql()

        then:
        assertCollectionsEqual(result.instructions, instructions)
        result.sql == SqlType.MICROSOFT_SQL.sqlDialect
    }

    @Unroll
    def "::toSql(SqlType) returns SqlResult with instructions and specified SQL for #type"(SqlType type) {
        when:
        def result = new Result(instructions).toSql(type)

        then:
        assertCollectionsEqual(result.instructions, instructions)
        result.sql == type.sqlDialect

        where:
        type << SqlType.values()
    }

    def "::toSql(Sql) returns SqlResult with instructions and specified SQL"() {
        given:
        def sql = Mock(Sql)

        when:
        def result = new Result(instructions).toSql(sql)

        then:
        assertCollectionsEqual(result.instructions, instructions)
        result.sql == sql
    }
}
