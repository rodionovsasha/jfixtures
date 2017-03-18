package com.github.vkorobkov.jfixtures.sql.appenders

import spock.lang.Specification

class StringAppenderTest extends Specification {

    StringAppender appender

    void setup() {
        appender = new StringAppender()
    }

    def "returns empty string on a new appender"() {
        expect:
        appender.toString().empty
    }

    def "accumulates appended pieces"() {
        when:
        appender.append("Hello, ")
        appender.append("World!")

        then:
        appender as String == "Hello, World!"
    }

    def "it is possible to call toString() many times"() {
        when:
        appender.append("Hello, ")

        then:
        appender as String == "Hello, "

        and:
        appender.append("World!")

        then:
        appender as String == "Hello, World!"
    }
}
