package com.github.vkorobkov.jfixtures.processor.sequence

import com.github.vkorobkov.jfixtures.loader.FixtureValue
import spock.lang.Specification

class IncrementalSequenceTest extends Specification {

    IncrementalSequence sequence

    void setup() {
        sequence = new IncrementalSequence()
    }

    def "increments from the LOWER_BOUND with step=1"() {
        expect:
        sequence.next("vlad") == new FixtureValue(sequence.LOWER_BOUND)

        and:
        sequence.next("vlad") == new FixtureValue(sequence.LOWER_BOUND + 1)

        and:
        sequence.next("vlad") == new FixtureValue(sequence.LOWER_BOUND + 2)
    }

    def "row name value does not matter"() {
        expect:
        sequence.next("vlad") == new FixtureValue(sequence.LOWER_BOUND)

        and:
        sequence.next(null) == new FixtureValue(sequence.LOWER_BOUND + 1)

        and:
        sequence.next("hero") == new FixtureValue(sequence.LOWER_BOUND + 2)
    }
}
