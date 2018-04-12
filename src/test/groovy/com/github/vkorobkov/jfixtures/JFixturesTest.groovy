package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.domain.Row
import com.github.vkorobkov.jfixtures.domain.Table
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

class JFixturesTest extends Specification implements YamlVirtualFolder {
    def "#ofConfig instantiates object with config path stored"() {
        expect:
        JFixtures.ofConfig("path/.conf").config == Optional.of("path/.conf")
    }

    def "#noConfig instantiates object with empty config"() {
        expect:
        JFixtures.noConfig().config == Optional.empty()
    }

    def "instantiated object has read only tables collection"() {
        given:
        def tables = JFixtures.noConfig().tables

        when:
        tables.add(new Table("users", Collections.emptyList()))

        then:
        thrown(UnsupportedOperationException)
    }

    def "#addTables(Collection<Table>) adds new tables and returns another instance"() {
        given:
        def tablesToAdd = [
            new Table("users", Collections.emptyList()),
            new Table("comments", Collections.emptyList())
        ]
        def fixtures = JFixtures.noConfig()

        when:
        def withTables = fixtures.addTables(tablesToAdd)

        then:
        withTables.tables.toListString() == tablesToAdd.toListString()

        and:
        !withTables.is(fixtures)
    }

    def "#addTables(Collection<Table>) could be called in chain accumulating tables"() {
        given:
        def tablesToAdd = [
                new Table("users", Collections.emptyList()),
                new Table("comments", Collections.emptyList())
        ]
        def fixtures = JFixtures.noConfig()

        when:
        fixtures = fixtures.addTables(tablesToAdd).addTables(tablesToAdd)

        then:
        fixtures.tables.toListString() == (tablesToAdd + tablesToAdd).toListString()
    }

    def "#addTables(Collection<Table>) allows empty list"() {
        expect:
        JFixtures.noConfig().addTables(Collections.emptyList()).tables.size() == 0
    }

    def "#addTables(Table...) adds new tables and returns another instance"() {
        given:
        def tablesToAdd = [
                new Table("users", Collections.emptyList()),
                new Table("comments", Collections.emptyList())
        ] as Table[]
        def fixtures = JFixtures.noConfig()

        when:
        def withTables = fixtures.addTables(tablesToAdd)

        then:
        withTables.tables.toListString() == tablesToAdd.toList().toListString()

        and:
        !withTables.is(fixtures)
    }

    def "#addTables(Table...) could be called in chain accumulating tables"() {
        given:
        def tablesToAdd = [
                new Table("users", Collections.emptyList()),
                new Table("comments", Collections.emptyList())
        ] as Table[]
        def fixtures = JFixtures.noConfig()

        when:
        fixtures = fixtures.addTables(tablesToAdd).addTables(tablesToAdd)

        then:
        fixtures.tables.toListString() == (tablesToAdd.toList() + tablesToAdd.toList()).toListString()
    }

    def "#addTables(Table...) allows empty list"() {
        expect:
        JFixtures.noConfig().addTables([] as Table[]).tables.size() == 0
    }

    def "#load adds fixtures stored in the directory"() {
        setup:
        def path = unpackYamlToTempFolder("default.yml")

        when:
        def tables = JFixtures.noConfig().loadDirectory(path.toString()).tables

        then:
        tables.size() == 1

        and:
        def table = tables.first()
        table.name == "users"
        table.rows.toList() == [
            new Row("vlad", [id: 1, name: "Vlad", age: 30]),
            new Row("homer", [id: 2, name: "Homer", age: 40])
        ]

        cleanup:
        path.toFile().deleteDir()
    }
}
