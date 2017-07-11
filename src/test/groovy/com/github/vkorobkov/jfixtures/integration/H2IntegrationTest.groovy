package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.JFixtures
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import groovy.sql.Sql
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

class H2IntegrationTest extends Specification implements YamlVirtualFolder {
    @Shared
    Properties properties = new Properties()
    @Shared
    File propertiesFile = new File(getClass().getClassLoader().getResource("jfixtures-test.properties").toURI())

    Path tmpFolderPath = unpackYamlToTempFolder("default.yml")

    @Shared
    Sql sql

    void setupSpec() {
        properties.load(propertiesFile.newDataInputStream())
        sql = connectToDb()
        sql.execute("CREATE TABLE users (ID INT PRIMARY KEY, NAME varchar(50) DEFAULT NULL, AGE INT DEFAULT NULL)")
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

    private Sql connectToDb() {
        Sql.newInstance(properties.getProperty("datasource.url"), properties.getProperty("datasource.username"), properties.getProperty("datasource.password"), properties.getProperty("datasource.driver-class-name"))
    }
}