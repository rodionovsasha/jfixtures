package com.github.vkorobkov.jfixtures.config.structure.util

import spock.lang.Specification

class SplitStringConsumerTest extends Specification {

    def "splits comma separated strings"() {
        expect:
        accept("1,2,3") == ["1", "2", "3"]
    }

    def "result items are trimmed"() {
        expect:
        accept(" 1 ,   2 ,  3   ") == ["1", "2", "3"]
    }

    def "trailing and leading commas are ignored"() {
        expect:
        accept(", 1, 2, 3,  ") == ["1", "2", "3"]
    }

    def "returns a single value"() {
        expect:
        accept("1") == ["1"]
    }

    def accept(input) {
        def result = []
        def consumer = new SplitStringConsumer({ result << it })
        consumer.accept(input)
        result
    }
}
