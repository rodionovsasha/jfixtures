package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.testutil.H2Test
import spock.lang.Specification

import java.nio.file.Path

class CleanTableIntegrationTest extends Specification implements H2Test {
    Path tmpCleanFolder = unpackYamlToTempFolder("clean_table.yml")

    void setup() {
        recreateTable("friends", "ID INT, NAME VARCHAR(50), AGE INT")
        recreateTable("mates", "ID INT, NAME VARCHAR(50), AGE INT")
        recreateTable("users", "ID INT, NAME VARCHAR(50), AGE INT")
    }

    def cleanup() {
        tmpCleanFolder.toFile().deleteDir()
    }

    def "should delete from table by default"() {
        given:
        executeFixtures(tmpCleanFolder)

        def result = sql.rows("SELECT * FROM mates")
        result == [[ID:10000, NAME: "Igor", AGE: 31]]

        when:
        executeFixtures(tmpCleanFolder)

        then:
        def secondResult = sql.rows("SELECT * FROM mates")
        secondResult == [[ID:10000, NAME: "Igor", AGE: 31]]
    }

    def "should delete from table when clean_method has 'delete' value"() {
        given:
        executeFixtures(tmpCleanFolder)

        def result = sql.rows("SELECT * FROM friends")
        result == [[ID:10000, NAME: "Semen", AGE: 30]]

        when:
        executeFixtures(tmpCleanFolder)

        then:
        def secondResult = sql.rows("SELECT * FROM friends")
        secondResult == [[ID:10000, NAME: "Semen", AGE: 30]]
    }

    def "should not delete from table when clean_method has 'none' value"() {
        given:
        executeFixtures(tmpCleanFolder)

        def result = sql.rows("SELECT * FROM users")
        result == [[ID:10000, NAME: "Vladimir", AGE: 29]]

        when:
        executeFixtures(tmpCleanFolder)

        then:
        def secondResult = sql.rows("SELECT * FROM users")
        secondResult == [[ID:10000, NAME: "Vladimir", AGE: 29], [ID:10000, NAME: "Vladimir", AGE: 29]]
    }
}
