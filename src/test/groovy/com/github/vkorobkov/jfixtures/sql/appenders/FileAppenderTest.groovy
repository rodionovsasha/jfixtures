package com.github.vkorobkov.jfixtures.sql.appenders

import com.github.vkorobkov.jfixtures.testutil.WithTempFile
import spock.lang.Specification

class FileAppenderTest extends Specification implements WithTempFile {

    String tmpFilePath

    void setup() {
        tmpFilePath = newTempFile("output.sql") as String
    }

    def "appends text to a given file"() {
        given:
        def appender = new FileAppender(tmpFilePath)

        when:
        appender.append("Hello, ")
        appender.append("World!")

        and:
        appender.close()

        then:
        new File(tmpFilePath).text == "Hello, World!"
    }

    def "rewrites old file"() {
        given:
        def appender = new FileAppender(tmpFilePath)
        appender.append("Hello, ")
        appender.close()

        when:
        appender = new FileAppender(tmpFilePath)
        appender.append("World!")
        appender.close()

        then:
        new File(tmpFilePath).text == "World!"
    }
}
