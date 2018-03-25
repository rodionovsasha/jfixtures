package com.github.vkorobkov.jfixtures.loader

import com.github.vkorobkov.jfixtures.domain.Value
import spock.lang.Specification

class MapRowsLoaderTest extends Specification {

    def simpleRows = [
        vlad:   [first_name: "Vladimir",    age: 29, sex: "man"],
        diman:  [first_name: "Dmitry",      age: 28, sex: "man"]
    ]

    def "load simple fixture test"() {
        when:
        def fixtures = loadRows(simpleRows)

        then:
        fixtures.size() == 2

        and:
        def vlad = fixtures[0]
        vlad.name == "vlad"
        vlad.columns.first_name == new Value("Vladimir")
        vlad.columns.age == new Value(29)
        vlad.columns.sex == new Value("man")

        and:
        def dima = fixtures[1]
        dima.name == "diman"
        dima.columns.first_name == new Value("Dmitry")
        dima.columns.age == new Value(28)
        dima.columns.sex == new Value("man")
    }

    def "columns remain the ordering"() {
        when:
        def fixtures = loadRows(simpleRows)

        then:
        def columnsKeys = fixtures[0].columns.keySet().asList()
        columnsKeys == ["first_name", "age", "sex"]
    }

    def "no rows when loads an empty fixture"() {
        when:
        def fixtures = loadRows([:])

        then:
        fixtures.empty
    }

    def "no columns when row is empty in the fixture"() {
        when:
        def fixtures = loadRows(vlad: [first_name: "Vladimir", age: 29, sex: "man"], diman: [:])

        then:
        fixtures.size() == 2

        and:
        def vlad = fixtures[0]
        vlad.name == "vlad"
        vlad.columns.first_name == new Value("Vladimir")
        vlad.columns.age == new Value(29)
        vlad.columns.sex == new Value("man")

        and:
        def dima = fixtures[1]
        dima.name == "diman"
        dima.columns.isEmpty()
    }

    def "loads explicitly defined SQL type and explicitly auto type"() {
        when:
        def fixtures = loadRows(vlad: [first_name: "Vladimir", age: "sql:DEFAULT"])

        then:
        def vlad = fixtures.first()
        vlad.name == "vlad"
        vlad.columns.first_name == new Value("Vladimir")
        vlad.columns.age == new Value("sql:DEFAULT")
    }

    private def loadRows(rows) {
        new MapRowsLoader(rows).load()
    }
}
