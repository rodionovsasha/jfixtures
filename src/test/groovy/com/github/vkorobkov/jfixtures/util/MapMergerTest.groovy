package com.github.vkorobkov.jfixtures.util

import spock.lang.Specification


class MapMergerTest extends Specification {

    def "dummy constructor test"() {
        expect:
        new MapMerger()
    }

    def "replaces the leaf nodes on the root level"() {
        given:
        def from = [name: "Vlad", age: 29, lang: "Groovy"]
        def with = [age: 30, lang: "Java"]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [name: "Vlad", age: 30, lang: "Java"]
    }

    def "adds leaf nodes on the root level"() {
        given:
        def from = [name: "Vlad", age: 29]
        def with = [lang: "Java"]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [name: "Vlad", age: 29, lang: "Java"]
    }

    def "adds subnodes on the root level"() {
        given:
        def from = [name: "Vlad", age: 29]
        def with = [langs: [first: "Java", second: "Groovy"]]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
    }

    def "merges subnodes content"() {
        given:
        def from = [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
        def with = [langs: [second: "Basic", third: "Ruby"]]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [name: "Vlad", age: 29, langs: [first: "Java", second: "Basic", third: "Ruby"]]
    }

    def "returns leaf when merging leaf into noe"() {
        given:
        def from = [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
        def with = [langs: "Basic"]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [name: "Vlad", age: 29, langs: "Basic"]
    }

    def "returns node when merge node into leaf"() {
        given:
        def from = [name: "Vlad", age: 29, langs: "Basic"]
        def with = [langs: [first: "Java", second: "Groovy"]]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
    }

    def "mutating of result does not affect the source lists"() {
        given:
        def from = [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
        def with = [langs: [third: "Ruby"], companies: [current: "Credo", past: "Wiley", next: "transisland"]]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [
                name     : "Vlad",
                age      : 29,
                langs    : [first: "Java", second: "Groovy", third: "Ruby"],
                companies: [current: "Credo", past: "Wiley", next: "transisland"]
        ]

        and:
        result.remove("age") == 29
        result.langs.remove("third") == "Ruby"
        result.companies.remove("current") == "Credo"

        and:
        from == [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
        with == [langs: [third: "Ruby"], companies: [current: "Credo", past: "Wiley", next: "transisland"]]
    }

    def "merges well empty map with non empty map"() {
        given:
        def from = [:]
        def with = [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
    }

    def "merges well non empty map with empty map"() {
        given:
        def from = [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
        def with = [:]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result == [name: "Vlad", age: 29, langs: [first: "Java", second: "Groovy"]]
    }

    def "preserves the source maps order"() {
        given:
        def from = [name: "Vlad", age: 29, lang: "Groovy"]
        def with = [age: 30, lang: "Java"]

        when:
        def result = MapMerger.merge(from, with)

        then:
        result.toMapString() == [name: "Vlad", age: 30, lang: "Java"].toMapString()
    }

    def "returns empty map when merges two empty maps"() {
        expect:
        MapMerger.merge([:], [:]).isEmpty()
    }
}
