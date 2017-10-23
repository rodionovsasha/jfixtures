package com.github.vkorobkov.jfixtures.processor

import spock.lang.Specification

class CircularPreventerTest extends Specification {
    CircularPreventer preventer
    CircularPreventer.Callback callback

    void setup() {
        preventer = new CircularPreventer()
        callback = Mock(CircularPreventer.Callback)
    }

    def "does not fail if there is no circular dependency"() {
        when:
        preventer.doInStack("users", callback)
        preventer.doInStack("permissions", callback)
        preventer.doInStack("comments", callback)

        then:
        3 * callback.doInStack()
    }

    def "fails on circular dependency"() {
        when:
        preventer.doInStack("users", {
            preventer.doInStack("users", callback)
        })

        then:
        def e = thrown(ProcessorException)
        e.message == "Circular dependency between tables found: users-->users"
    }

    def "fails on transitive circular dependency"() {
        when:
        preventer.doInStack("users", {
            preventer.doInStack("permissions", { preventer.doInStack("users", callback) })
        })

        then:
        def e = thrown(ProcessorException)
        e.message == "Circular dependency between tables found: users-->permissions-->users"
    }
}
