package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.testutil.H2Test
import spock.lang.Specification

import java.nio.file.Path

class H2IntegrationTest extends Specification implements H2Test {
    Path tmpFolderPath = unpackYamlToTempFolder("default.yml")
    Path tmpFolderPathPk = unpackYamlToTempFolder("pk.yml")

    void setupSpec() {
        recreateTable("users", "ID INT, NAME VARCHAR(50) DEFAULT NULL, AGE INT DEFAULT NULL")
        recreateTable("friends", "NAME VARCHAR(50) DEFAULT NULL, AGE INT DEFAULT NULL")
        recreateTable("mates", "ID INT, NAME VARCHAR(50) DEFAULT NULL, AGE INT DEFAULT NULL")
        recreateTable("pals", "ID INT, NAME VARCHAR(50) DEFAULT NULL, AGE INT DEFAULT NULL")
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
        tmpFolderPathPk.toFile().deleteDir()
    }

    def "h2 insert test"() {
        when:
        executeFixtures(tmpFolderPath)

        then:
        def result = sql.rows("SELECT * FROM users")
        result == [[ID: 1, NAME: "Vlad", AGE: 29], [ID: 10001, NAME: "Semen's special name:'#\"*[@;", AGE: 32]]
    }

    def "h2 insert without pk test"() {
        when:
        executeFixtures(tmpFolderPathPk)

        then:
        def result = sql.rows("SELECT * FROM friends")
        result == [[NAME: "Vlad", AGE: 29]]
    }

    def "h2 insert with test with auto generated pk"() {
        when:
        executeFixtures(tmpFolderPathPk)

        then:
        def result = sql.rows("SELECT * FROM mates")
        result == [[ID: 10000, NAME: "Vlad", AGE: 29]]
    }

    def "h2 insert with test with auto generated pk when autoGeneratePk flag does not exist"() {
        when:
        executeFixtures(tmpFolderPathPk)

        then:
        def result = sql.rows("SELECT * FROM pals")
        result == [[ID: 10000, NAME: "Vlad", AGE: 29]]
    }
}