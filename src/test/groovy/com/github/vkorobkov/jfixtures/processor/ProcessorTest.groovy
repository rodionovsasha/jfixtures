package com.github.vkorobkov.jfixtures.processor

import com.github.vkorobkov.jfixtures.config.Config
import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.instructions.Instruction
import com.github.vkorobkov.jfixtures.loader.FixtureValue
import com.github.vkorobkov.jfixtures.loader.FixturesLoader
import com.github.vkorobkov.jfixtures.processor.sequence.IncrementalSequence
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

class ProcessorTest extends Specification implements YamlVirtualFolder {

    def "basic row creation instructions test"() {
        when:
        def instructions = load("basic_fixtures.yml")

        then:
        instructions.size() == 3

        and:
        (instructions[0] as CleanTable).table == "admin.users"

        and:
        def vlad = instructions[1] as InsertRow
        vlad.table == "admin.users"
        vlad.rowName == "vlad"
        assertInsertInstructions(vlad.values, [
            id: IncrementalSequence.LOWER_BOUND,
            first_name: "Vladimir",
            age: 29,
            sex: "man"
        ])

        and:
        def diman = instructions[2] as InsertRow
        diman.table == "admin.users"
        diman.rowName == "diman"
        assertInsertInstructions(diman.values, [
            id: IncrementalSequence.LOWER_BOUND + 1,
            first_name: "Dmitry",
            age: 28,
            sex: "man"
        ])
    }

    def "cleans up empty table as well"() {
        when:
        def instructions = load("one_empty_table.yml")

        then:
        instructions.size() == 1

        and:
        (instructions.first() as CleanTable).table == "users"
    }

    def "duplicate rows getting overridden by the latest one"() {
        when:
        def instructions = load("duplicate_rows.yml")

        then:
        instructions.size() == 2

        and:
        def vlad = instructions[1] as InsertRow
        vlad.table == "users"
        vlad.rowName == "vlad"
        assertInsertInstructions(vlad.values, [
            id: IncrementalSequence.LOWER_BOUND,
            first_name: "Dmitry",
            age: 28,
            sex: "man"
        ])
    }

    def "primary key is not getting overridden if it was explicitly defined in the table"() {
        when:
        def instructions = load("with_defined_pk.yml")

        then:
        instructions.size() == 2

        and:
        def vlad = instructions[1] as InsertRow
        vlad.values.id == FixtureValue.ofAuto(100500)
    }

    def "resolves basic dependencies"() {
        when:
        def instructions = load("basic_dependencies.yml")

        then:
        def insertions = instructions.findAll { it instanceof InsertRow }

        then:
        def users_to_roles = insertions.findAll { it.table == "admin.users_to_roles" }
        users_to_roles.size() == 3

        and:
        def roles = insertions.findAll { it.table == "admin.roles" }
        roles.size() == 3

        and:
        def users = insertions.findAll { it.table == "admin.users" }
        users.size() == 3

        and:
        users_to_roles.every {
            def index = insertions.indexOf(it)
            roles.every { insertions.indexOf(it) < index } && users.every { insertions.indexOf(it) < index }
        }

        and:
        users_to_roles[0].values == [
            id: FixtureValue.ofAuto(IncrementalSequence.LOWER_BOUND),
            user_id: users.find { it.rowName == "kirill" }.values.id,
            role_id: roles.find { it.rowName == "guest" }.values.id,
        ]
        users_to_roles[1].values == [
            id: FixtureValue.ofAuto(IncrementalSequence.LOWER_BOUND + 1),
            user_id: users.find { it.rowName == "vlad" }.values.id,
            role_id: roles.find { it.rowName == "owner" }.values.id,
        ]
        users_to_roles[2].values == [
            id: FixtureValue.ofAuto(IncrementalSequence.LOWER_BOUND + 2),
            user_id: users.find { it.rowName == "diman" }.values.id,
            role_id: roles.find { it.rowName == "commitee" }.values.id,
        ]
    }

    def "throws when dependent table is not found"() {
        when:
        load("dependent_table_not_found.yml")

        then:
        def exception = thrown(ProcessorException)
        exception.message.contains("Referred table [users] is not found")
    }

    def "throws when dependent row is not found"() {
        when:
        load("dependent_row_not_found.yml")

        then:
        def exception = thrown(ProcessorException)
        exception.message.contains("Referred row [users.kirill] is not found")
    }

    def "parent-child relation inside the same table"() {
        when:
        def instructions = load("parent_child_dep.yml")

        then:
        def insertions = instructions.findAll { it instanceof InsertRow }

        and:
        def grandfa = insertions.find { it.rowName == "grand_fa" }
        grandfa.values.parent_id == null

        and:
        def dad = insertions.find { it.rowName == "dad" }
        def uncle = insertions.find { it.rowName == "uncle" }
        dad.values.parent_id == grandfa.values.id
        uncle.values.parent_id == grandfa.values.id

        and:
        def me = insertions.find { it.rowName == "me" }
        me.values.parent_id == dad.values.id
    }

    def "fails on direct circular dependency"() {
        when:
        load("direct_circular_dependency.yml")

        then:
        def exception = thrown(ProcessorException)
        exception.message.contains("Circular dependency between tables found")
    }

    def "fails on transitive circular dependency"() {
        when:
        load("transitive_circular_dependency.yml")

        then:
        def exception = thrown(ProcessorException)
        exception.message.contains("Circular dependency between tables found")
    }

    def "row creation instructions test with PK disabled"() {
        when:
        def instructions = load("basic_fixtures_with_pk_disabled.yml")

        then:
        instructions.size() == 2

        and:
        (instructions[0] as CleanTable).table == "users"

        and:
        def vlad = instructions[1] as InsertRow
        vlad.table == "users"
        vlad.rowName == "vlad"
        assertInsertInstructions(vlad.values, [
                first_name: "Vladimir",
                age: 29,
                sex: "man"
        ])
    }

    boolean assertInsertInstructions(Map instructions, Map expected) {
        expected = expected.collectEntries { [it.key, FixtureValue.ofAuto(it.value)] }
        new LinkedHashMap(instructions) == expected
    }

    List<Instruction> load(String ymlFile) {
        def path = unpackYamlToTempFolder(ymlFile) as String
        def config = new Config(path)
        def fixtures = new FixturesLoader(path, config).load()
        new Processor(fixtures, config).process()
    }
}
