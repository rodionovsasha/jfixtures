package com.github.vkorobkov.jfixtures.loader

import com.github.vkorobkov.jfixtures.domain.Value
import spock.lang.Specification

import java.lang.reflect.Modifier

class MapDataLoaderTest extends Specification {
    def rows = [
            vlad : [first_name: "Vladimir", age: 29, sex: "man"],
            diman: [first_name: "Dmitry", age: 28, sex: "man"]
    ]

    def tablesWithRows = [
            users   : [
                    vlad : [id: 5, age: 30],
                    homer: [id: 6, age: 39]
            ],
            comments: [:]
    ]

    def 'private constructor test'() {
        when:
        def constructors = MapDataLoader.class.getDeclaredConstructors()

        then:
        constructors.size() == 1
        Modifier.isPrivate(constructors[0].getModifiers())
    }

    def "::loadRows loads simple fixture test"() {
        when:
        def fixtures = MapDataLoader.loadRows(rows)

        then:
        fixtures.size() == 2

        and:
        def vlad = fixtures[0]
        vlad.name == "vlad"
        vlad.columns.first_name == Value.of("Vladimir")
        vlad.columns.age == Value.of(29)
        vlad.columns.sex == Value.of("man")

        and:
        def dima = fixtures[1]
        dima.name == "diman"
        dima.columns.first_name == Value.of("Dmitry")
        dima.columns.age == Value.of(28)
        dima.columns.sex == Value.of("man")
    }

    def "columns remain the ordering"() {
        when:
        def fixtures = MapDataLoader.loadRows(rows)

        then:
        def columnsKeys = fixtures[0].columns.keySet().asList()
        columnsKeys == ["first_name", "age", "sex"]
    }

    def "no rows when loads an empty fixture"() {
        when:
        def fixtures = MapDataLoader.loadRows([:])

        then:
        fixtures.empty
    }

    def "no rows when loads null"() {
        when:
        def fixtures = MapDataLoader.loadRows(null)

        then:
        fixtures.empty
    }

    def "no columns when row is empty in the fixture"() {
        when:
        def fixtures = MapDataLoader.loadRows(vlad: [first_name: "Vladimir", age: 29, sex: "man"], diman: [:])

        then:
        fixtures.size() == 2

        and:
        def vlad = fixtures[0]
        vlad.name == "vlad"
        vlad.columns.first_name == Value.of("Vladimir")
        vlad.columns.age == Value.of(29)
        vlad.columns.sex == Value.of("man")

        and:
        def dima = fixtures[1]
        dima.name == "diman"
        dima.columns.isEmpty()
    }

    def "::loadRows explicitly defined SQL type and explicitly auto type"() {
        when:
        def fixtures = MapDataLoader.loadRows(vlad: [first_name: "Vladimir", age: "sql:DEFAULT"])

        then:
        def vlad = fixtures.first()
        vlad.name == "vlad"
        vlad.columns.first_name == Value.of("Vladimir")
        vlad.columns.age == Value.ofSql("DEFAULT")
    }

    def "::loadTables loads tables with rows fixture test"() {
        when:
        def fixtures = MapDataLoader.loadTables(tablesWithRows)

        then:
        fixtures.size() == 2

        and:
        def users = fixtures[0]
        users.name == 'users'
        users.rows.size() == 2

        and:
        def vlad = users.rows[0]
        vlad.name == 'vlad'
        vlad.columns.id == Value.of(5)
        vlad.columns.age == Value.of(30)

        and:
        def homer = users.rows[1]
        homer.name == 'homer'
        homer.columns.id == Value.of(6)
        homer.columns.age == Value.of(39)

        and:
        def comments = fixtures[1]
        comments.name == 'comments'
        comments.rows.size() == 0
    }

    def "::loadTables loads only tables"() {
        when:
        def tables = [
                users   : [:],
                comments: [:]
        ]
        def fixtures = MapDataLoader.loadTables(tables)

        then:
        fixtures.size() == 2

        and:
        def users = fixtures[0]
        users.name == 'users'
        users.rows.size() == 0

        and:
        def comments = fixtures[1]
        comments.name == 'comments'
        comments.rows.size() == 0
    }

    def "no tables when loads an empty fixture"() {
        when:
        def fixtures = MapDataLoader.loadTables([:])

        then:
        fixtures.empty
    }

    def "no tables when loads null"() {
        when:
        def fixtures = MapDataLoader.loadTables(null)

        then:
        fixtures.empty
    }
}
