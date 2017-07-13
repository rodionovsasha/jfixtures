package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.testutil.H2Test
import spock.lang.Specification

import java.nio.file.Path

class H2IntegrationTest extends Specification implements H2Test {
    Path tmpFolderPath = unpackYamlToTempFolder("default.yml")

    void setupSpec() {
        sql.execute("DROP TABLE IF EXISTS users")
        sql.execute("CREATE TABLE users (ID INT PRIMARY KEY, NAME varchar(50) DEFAULT NULL, AGE INT DEFAULT NULL)")
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
    }

    def "h2 insert test"() {
        when:
        executeFixtures(tmpFolderPath)

        then:
        def query = "SELECT * FROM users"
        sql.eachRow(query) { row ->
            assert "$row.id" == "1"
            assert "$row.name" == "Vlad"
            assert "$row.age" == "29"
        }
    }
}