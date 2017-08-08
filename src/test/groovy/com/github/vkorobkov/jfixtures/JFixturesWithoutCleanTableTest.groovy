package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class JFixturesWithoutCleanTableTest extends Specification implements YamlVirtualFolder {
    Path tmpFolderPath
    String outputPath

    def DEFAULT_EXPECTED_SQL = """DELETE FROM "mates";
            |INSERT INTO "mates" ("id", "first_name", "age") VALUES (10000, 'Igor', 31);
            |INSERT INTO "users" ("id", "name", "age") VALUES (10000, 'Vlad', 29);
            |DELETE FROM "friends";
            |INSERT INTO "friends" ("id", "first_name", "age") VALUES (10000, 'Semen', 30);
            |""".stripMargin()

    void setup() {
        tmpFolderPath = unpackYamlToTempFolder("default.yml")
        outputPath = tmpFolderPath.resolve("out.sql") as String
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
    }

    def "should add delete instruction by default or when clean_method has 'delete' value"() {
        expect:
        JFixtures.postgres(tmpFolderPath as String).asString() == DEFAULT_EXPECTED_SQL
    }
}
