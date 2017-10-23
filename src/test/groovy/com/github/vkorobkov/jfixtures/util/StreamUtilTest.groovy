package com.github.vkorobkov.jfixtures.util

import spock.lang.Specification

import java.util.stream.Collectors

class StreamUtilTest extends Specification {

    def "dummy constructor test"() {
        expect:
        new StreamUtil()
    }

    def "converts list do map"() {
        given:
        def people = [new Dummy(name: "Vlad", age: 29), new Dummy(name: "Diman", age: 28)]

        when:
        def result = people.stream().collect(mapCollector()) as Map

        then:
        result.size() == 2

        and:
        result == ["Vlad": 29, "Diman": 28]
    }

    def "duplicate key issues an exception when convert to map"() {
        given:
        def people = [new Dummy(name: "Vlad", age: 29), new Dummy(name: "Vlad", age: 28)]

        when:
        people.stream().collect(mapCollector())

        then:
        thrown(IllegalStateException)
    }

    static mapCollector() {
        Collectors.toMap({ Dummy d -> d.name }, { Dummy d -> d.age }, StreamUtil.throwingMerger(),
                { new LinkedHashMap() })
    }

    static class Dummy {
        String name
        int age
    }
}
