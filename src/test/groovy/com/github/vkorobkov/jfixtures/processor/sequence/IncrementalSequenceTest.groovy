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
        sequence.next("vlad") == FixtureValue.ofAuto(sequence.LOWER_BOUND)

        and:
        sequence.next("vlad") == FixtureValue.ofAuto(sequence.LOWER_BOUND + 1)

        and:
        sequence.next("vlad") == FixtureValue.ofAuto(sequence.LOWER_BOUND + 2)
    }

    def "row name value does not matter"() {
        expect:
        sequence.next("vlad") == FixtureValue.ofAuto(sequence.LOWER_BOUND)

        and:
        sequence.next(null) == FixtureValue.ofAuto(sequence.LOWER_BOUND + 1)

        and:
        sequence.next("hero") == FixtureValue.ofAuto(sequence.LOWER_BOUND + 2)
    }
}
