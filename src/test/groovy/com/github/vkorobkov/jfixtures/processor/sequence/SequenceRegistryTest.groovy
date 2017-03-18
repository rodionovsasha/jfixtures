package com.github.vkorobkov.jfixtures.processor.sequence

import com.github.vkorobkov.jfixtures.loader.FixtureValue
import spock.lang.Specification

class SequenceRegistryTest extends Specification {

    SequenceRegistry registry

    void setup() {
        registry = new SequenceRegistry()
        registry.put("users", new IncrementalSequence())
        registry.put("users", new IncrementalSequence())
        registry.put("idiots", new IncrementalSequence())
    }

    def "increments sequence for specific table"() {
        expect:
        registry.nextValue("users", "vlad") == new FixtureValue(IncrementalSequence.LOWER_BOUND)
        registry.nextValue("users", "diman") == new FixtureValue(IncrementalSequence.LOWER_BOUND + 1)
        registry.nextValue("users", "kirill") == new FixtureValue(IncrementalSequence.LOWER_BOUND + 2)
    }

    def "another table returns a new sequence"() {
        expect:
        registry.nextValue("users", "vlad") == new FixtureValue(IncrementalSequence.LOWER_BOUND)
        registry.nextValue("users", "diman") == new FixtureValue(IncrementalSequence.LOWER_BOUND + 1)
        registry.nextValue("users", "kirill") == new FixtureValue(IncrementalSequence.LOWER_BOUND + 2)

        and:
        registry.nextValue("idiots", "n") == new FixtureValue(IncrementalSequence.LOWER_BOUND)
        registry.nextValue("idiots", "m") == new FixtureValue(IncrementalSequence.LOWER_BOUND + 1)
        registry.nextValue("idiots", "k") == new FixtureValue(IncrementalSequence.LOWER_BOUND + 2)
    }
}
