package com.github.vkorobkov.jfixtures.sql

import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import spock.lang.Specification

class AppenderTest extends Specification {
    Appender appender

    void setup() {
        appender = Spy(StringAppender)
    }

    def "append_many calls single appends keeping the order"() {
        when:
        appender.append("one", "two", "three")

        then:
        1 * appender.append("one")
        1 * appender.append("two")
        1 * appender.append("three")
    }
}
