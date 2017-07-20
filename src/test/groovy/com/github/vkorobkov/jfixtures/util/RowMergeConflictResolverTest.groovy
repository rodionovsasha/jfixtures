package com.github.vkorobkov.jfixtures.util

import spock.lang.Specification


class RowMergeConflictResolverTest extends Specification {

    def "dummy constructor test"() {
        expect:
        RowMergeConflictResolver.INSTANCE
    }

    def "default value applies if new value was not specified"() {
        when:
        def from = "Vlad"
        def with = [age: 28]

        then:
        RowMergeConflictResolver.INSTANCE.apply(from, with) == [value: "Vlad", age: 28]
    }

    def "default value gets skipped if new value was specified"() {
        when:
        def from = "Vlad"
        def with = [type: "sql", value: "Vlad"]

        then:
        RowMergeConflictResolver.INSTANCE.apply(from, with) == [type: "sql", value: "Vlad"]
    }

    def "default row's value gets replaced with new value"() {
        when:
        def from = [type: "sql", value: "Vlad"]
        def with = "Vovan"

        then:
        RowMergeConflictResolver.INSTANCE.apply(from, with) == [type: "sql", value: "Vovan"]
    }

    def "new values gets added to default row"() {
        when:
        def from = [type: "sql"]
        def with = "Vovan"

        then:
        RowMergeConflictResolver.INSTANCE.apply(from, with) == [type: "sql", value: "Vovan"]
    }
}
