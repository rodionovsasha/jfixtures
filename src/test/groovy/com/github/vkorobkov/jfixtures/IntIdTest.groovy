package com.github.vkorobkov.jfixtures

import spock.lang.Specification

class IntIdTest extends Specification {

    def "dummy constructor test"() {
        expect:
        new IntId()
    }

    def "one returns the same hash for the same alias"() {
        expect:
        IntId.one(name) == IntId.one(name)

        where:
        name << ["homer", "bart", "maggy", "lisa", "marge"]
    }

    def "one returns only positive hashes"() {
        expect:
        IntId.one(alias) > 0

        where:
        alias << randomAliases
    }

    def "one returns hashes which are all >= LOWER_BOUND"() {
        expect:
        IntId.one(alias) >= IntId.LOWER_BOUND

        where:
        alias << randomAliases
    }

    def "#many returns the same hashes as #one does, keeping the order"() {
        expect:
        IntId.many("homer", "marge", "bart") == [
                IntId.one("homer"),
                IntId.one("marge"),
                IntId.one("bart")
        ]
    }

    def "#many returns the same hashes for the same aliases"() {
        when:
        def hashes = IntId.many("homer", "homer")

        then:
        hashes[0] == hashes[1]
    }

    def "#many returns empty list of hashes when no aliases passed"() {
        expect:
        IntId.many().empty
    }

    private def getRandomAliases() {
        (0..100).collect { UUID.randomUUID() as String }
    }
}
