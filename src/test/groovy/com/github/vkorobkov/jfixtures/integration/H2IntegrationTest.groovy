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
        def query = "SELECT * FROM users"
        def result = sql.rows(query)

        assert result.size() == 2
        assert result.get(0).get("ID") == 1
        assert result.get(0).get("NAME") == "Vlad"
        assert result.get(0).get("AGE") == 29

        assert result.get(1).get("ID") == 2
        assert result.get(1).get("NAME") == "Semen's name"
        assert result.get(1).get("AGE") == 32
    }
}