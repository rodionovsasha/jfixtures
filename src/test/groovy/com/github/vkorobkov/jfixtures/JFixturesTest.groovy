package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class JFixturesTest extends Specification implements YamlVirtualFolder {
    Path tmpFolderPath
    String outputPath

    def PG_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (1, 'Vlad', 29);
            |""".stripMargin()

    def MYSQL_EXPECTED_SQL = """DELETE FROM `users`;
            |INSERT INTO `users` (`id`, `name`, `age`) VALUES (1, 'Vlad', 29);
            |""".stripMargin()

    def H2_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (1, 'Vlad', 29);
            |""".stripMargin()

    void setup() {
        tmpFolderPath = unpackYamlToTempFolder("default.yml")
        outputPath = tmpFolderPath.resolve("out.sql") as String
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
    }

    def "dummy constructor"() {
        expect:
        new JFixtures()
    }

    def "postgres fixture to string"() {
        expect:
        JFixtures.postgres(tmpFolderPath as String).asString() == PG_EXPECTED_SQL
    }

    def "postgres fixture to file"() {
        when:
        JFixtures.postgres(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == PG_EXPECTED_SQL
    }

    def "mysql fixture to string"() {
        expect:
        JFixtures.mysql(tmpFolderPath as String).asString() == MYSQL_EXPECTED_SQL
    }

    def "mysql fixture to file"() {
        when:
        JFixtures.mysql(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == MYSQL_EXPECTED_SQL
    }

    def "h2 fixture to a string"() {
        expect:
        JFixtures.h2(tmpFolderPath as String).asString() == H2_EXPECTED_SQL
    }

    def "h2 fixture to a file"() {
        when:
        JFixtures.h2(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == H2_EXPECTED_SQL
    }
}