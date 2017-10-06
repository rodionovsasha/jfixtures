package com.github.vkorobkov.jfixtures

import spock.lang.Specification

class IdTest extends Specification {

    def "dummy constructor test"() {
        expect:
        new Id()
    }

    def "one returns the same hash for the same alias"() {
        expect:
        Id.one(name) == Id.one(name)

        where:
        name << ["homer", "bart", "maggy", "lisa", "marge"]
    }

    def "one returns only positive hashes"() {
        expect:
        Id.one(alias) > 0

        where:
        alias << randomAliases
    }

    def "one returns hashes which are all >= LOWER_BOUND"() {
        expect:
        Id.one(alias) >= Id.LOWER_BOUND

        where:
        alias << randomAliases
    }

    private def getRandomAliases() {
        (0..100).collect { UUID.randomUUID() as String }
    }
}
