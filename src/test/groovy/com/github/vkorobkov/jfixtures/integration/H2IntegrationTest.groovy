package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.JFixtures
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

import static com.github.vkorobkov.jfixtures.sql.DataSourceUtil.sql

class H2IntegrationTest extends Specification implements YamlVirtualFolder {
    Path tmpFolderPath = unpackYamlToTempFolder("default.yml")

    void setupSpec() {
        sql.execute("CREATE TABLE IF NOT EXISTS users (ID INT PRIMARY KEY, NAME varchar(50) DEFAULT NULL, AGE INT DEFAULT NULL)")
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
    }

    def "h2 insert test"() {
        when:
        sql.execute(JFixtures.h2(tmpFolderPath as String).asString())

        then:
        def query = "SELECT * FROM users"
        sql.eachRow(query) { row ->
            assert "$row.id" == "1"
            assert "$row.name" == "Vlad"
            assert "$row.age" == "29"
        }
    }
}