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
        sql.execute("INSERT INTO mates (\"id\", \"name\", \"age\") VALUES (10000, 'Igor', 31);")
        def result = sql.rows("SELECT * FROM mates")
        result.size() == 1

        when:
        executeFixtures(tmpCleanFolder)

        then:
        def secondResult = sql.rows("SELECT * FROM mates")
        secondResult.size() == 1
    }

    def "should delete from table when clean_method has 'delete' value"() {
        given:
        sql.execute("INSERT INTO friends (\"id\", \"name\", \"age\") VALUES (10000, 'Semen', 30);")
        def result = sql.rows("SELECT * FROM friends")
        result.size() == 1

        when:
        executeFixtures(tmpCleanFolder)

        then:
        def secondResult = sql.rows("SELECT * FROM friends")
        secondResult.size() == 1
    }

    def "should not delete from table when clean_method has 'none' value"() {
        given:
        sql.execute("INSERT INTO users (\"id\", \"name\", \"age\") VALUES (10000, 'Vladimir', 29);")
        def result = sql.rows("SELECT * FROM users")
        result.size() == 1

        when:
        executeFixtures(tmpCleanFolder)

        then:
        def secondResult = sql.rows("SELECT * FROM users")
        secondResult.size() == 2
    }
}
