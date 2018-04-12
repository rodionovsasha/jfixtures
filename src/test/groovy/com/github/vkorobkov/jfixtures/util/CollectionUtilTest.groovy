package com.github.vkorobkov.jfixtures.util

import spock.lang.Specification


class CollectionUtilTest extends Specification {

    def "dummy constructor test"() {
        expect:
        new CollectionUtil()
    }

    def "flattenRecursively() flattens nested arrays keeping the order"() {
        given:
        def input = [
            0, ["admins", "su", "roots"], [[1, 3, 5], [2, 4, 6]], 56, 12
        ]

        when:
        def result = flattenRecursively(input)

        then:
        result == [
            0, "admins", "su", "roots", 1, 3, 5, 2, 4, 6, 56, 12
        ]
    }

    def "flattenRecursively() returns an element if it is not a collection"() {
        expect:
        flattenRecursively(100) == [100]
    }

    def "flattenRecursively() returns a list if the input is a flat list"() {
        expect:
        flattenRecursively([1, 2, 3]) == [1, 2, 3]
    }

    def "flattenRecursively() does not flat a map"() {
        expect:
        flattenRecursively(name: "vlad", age: 29) == [[name: "vlad", age: 29]]
    }

    def "#mapValues changes every value in the map"() {
        given:
        def source = [vlad: 29, homer: 36]

        expect:
        CollectionUtil.mapValues(source, { it * 2 }) == [vlad: 58, homer: 72]
    }

    def "#mapValues returns empty map in the source map is empty"() {
        expect:
        CollectionUtil.mapValues([:], { it * 2}).isEmpty()
    }

    def "#mapValues preserves elements order"() {
        given:
        def source = [vlad: 30, homer: 40, bart: 10, marge: 40, lisa: 12]

        when:
        def keys = CollectionUtil.mapValues(source, { it }).keySet().toList()

        then:
        keys == ["vlad", "homer", "bart", "marge", "lisa"]
    }

    def "#merge adds or overwrites the source map with values of another map"() {
        given:
        def into = [name: "Vlad", age: 29]
        def with = [age: 30, born: 1988]

        expect:
        CollectionUtil.merge(into, with) == [name: "Vlad", age: 30, born: 1988]
    }

    def "#merge accepts immutable maps because it does not change arguments"() {
        given:
        def into = Collections.unmodifiableMap(name: "Vlad", age: 29)
        def with = Collections.unmodifiableMap(age: 30, born: 1988)

        expect:
        CollectionUtil.merge(into, with) == [name: "Vlad", age: 30, born: 1988]
    }

    def "#merge produces a new map, so changing the result does not change arguments"() {
        given:
        def into = [name: "Vlad", age: 29]
        def with = [age: 30, born: 1988]

        when:
        def result = CollectionUtil.merge(into, with)
        result["age"] = 100
        result.remove("born")

        then:
        into == [name: "Vlad", age: 29]
        with == [age: 30, born: 1988]
    }

    def "#merge preserves elements order"() {
        given:
        def part1 = [name: "Vlad", age: 30]
        def part2 = [bobby: "java", duration: 10]

        when:
        def result = CollectionUtil.merge(part1, part2)

        then:
        result == [name: "Vlad", age: 30, bobby: "java", duration: 10]
    }

    def "#concat concatenates two collections preserving eleemnts order"() {
        expect:
        CollectionUtil.concat([1, 2, 3], [4, 5, 6]).toListString() == [1, 2, 3, 4, 5, 6].toListString()
    }

    def "#concat returns unmodifiable collection"() {
        given:
        def result = CollectionUtil.concat([1, 2, 3], [4, 5, 6])

        when:
        result.add(7)

        then:
        thrown(UnsupportedOperationException)
    }

    def flattenRecursively(input) {
        def result = []
        CollectionUtil.flattenRecursively(input, { result << it })
        result
    }
}
