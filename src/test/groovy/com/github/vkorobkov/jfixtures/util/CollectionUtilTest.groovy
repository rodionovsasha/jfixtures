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

    def flattenRecursively(input) {
        def result = []
        CollectionUtil.flattenRecursively(input, { result << it })
        result
    }
}
