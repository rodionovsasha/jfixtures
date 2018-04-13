package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.IntId
import com.github.vkorobkov.jfixtures.testutil.H2Test
import spock.lang.Specification

import java.nio.file.Path

class H2IntegrationTest extends Specification implements H2Test {
    Path tmpDirectoryPath = unpackYamlToTempDirectory("default.yml")

    void setupSpec() {
        recreateTable("users", "ID INT, NAME VARCHAR(50) DEFAULT NULL, AGE INT DEFAULT NULL")
    }

    void cleanup() {
        tmpDirectoryPath.toFile().deleteDir()
    }

    def "h2 insert test"() {
        when:
        executeFixtures(tmpDirectoryPath)

        then:
        def result = sql.rows("SELECT * FROM users")
        result == [
            [ID: 1, NAME: "Vlad", AGE: 29],
            [ID: IntId.one("semen"), NAME: "Semen's special name:'#\"*[@;", AGE: 32]
        ]
    }
}