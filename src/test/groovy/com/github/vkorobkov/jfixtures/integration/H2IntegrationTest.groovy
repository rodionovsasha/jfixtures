package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.testutil.H2Test
import spock.lang.Specification

import java.nio.file.Path

class H2IntegrationTest extends Specification implements H2Test {
    Path tmpFolderPath = unpackYamlToTempFolder("default.yml")

    void setupSpec() {
        recreateTable("users", "ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(50) DEFAULT NULL, AGE INT DEFAULT NULL")
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
    }

    def "h2 insert test"() {
        when:
        executeFixtures(tmpFolderPath)

        then:
        def result = sql.rows("SELECT * FROM users")
        result == [[ID: 1, NAME: "Vlad", AGE: 29], [ID: 2, NAME: "Semen's special name:'#\"*[@;", AGE: 32]]
    }
}