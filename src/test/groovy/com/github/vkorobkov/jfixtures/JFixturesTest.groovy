package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class JFixturesTest extends Specification implements YamlVirtualFolder {
    Path tmlFolderPath
    String outputPath

    def PG_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (1, 'Vlad', 29);
            |""".stripMargin()

    def MYSQL_EXPECTED_SQL = """DELETE FROM `users`;
            |INSERT INTO `users` (`id`, `name`, `age`) VALUES (1, 'Vlad', 29);
            |""".stripMargin()

    void setup() {
        tmlFolderPath = unpackYamlToTempFolder("default.yml")
        outputPath = tmlFolderPath.resolve("out.sql") as String
    }

    void cleanup() {
        tmlFolderPath.toFile().deleteDir()
    }

    def "dummy constructor"() {
        expect:
        new JFixtures()
    }

    def "postgres fixture to string"() {
        expect:
        JFixtures.postgres(tmlFolderPath as String).asString() == PG_EXPECTED_SQL
    }

    def "postgres fixture to file"() {
        when:
        JFixtures.postgres(tmlFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == PG_EXPECTED_SQL
    }

    def "mysql fixture to string"() {
        expect:
        JFixtures.mysql(tmlFolderPath as String).asString() == MYSQL_EXPECTED_SQL
    }

    def "mysql fixture to file"() {
        when:
        JFixtures.mysql(tmlFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == MYSQL_EXPECTED_SQL
    }
}
