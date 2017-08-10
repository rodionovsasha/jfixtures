package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class BeforeInsertsTest extends Specification implements YamlVirtualFolder {
    Path tmpDefaultFolderPath
    Path tmpCustomFolderPath

    def DEFAULT_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (10000, 'Vladimir', 29);
            |""".stripMargin()
    def CUSTOM_EXPECTED_SQL = """DELETE FROM "users";
            |// Doing table users
            |BEGIN TRANSACTION;
            |INSERT INTO "users" ("id", "name", "age") VALUES (10000, 'Vladimir', 29);
            |""".stripMargin()

    void setup() {
        tmpDefaultFolderPath = unpackYamlToTempFolder("default.yml")
        tmpCustomFolderPath = unpackYamlToTempFolder("custom.yml")
    }

    void cleanup() {
        tmpDefaultFolderPath.toFile().deleteDir()
        tmpCustomFolderPath.toFile().deleteDir()
    }

    def "should not insert any custom SQL by default"() {
        expect:
        JFixtures.postgres(tmpDefaultFolderPath as String).asString() == DEFAULT_EXPECTED_SQL
    }

    def "should insert custom SQL if present"() {
        expect:
        JFixtures.postgres(tmpCustomFolderPath as String).asString() == CUSTOM_EXPECTED_SQL
    }
}
