package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

class BeforeInsertsTest extends Specification implements YamlVirtualDirectory {
    def DEFAULT_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (${IntId.one('vlad')}, 'Vladimir', 29);
            |""".stripMargin()
    def CUSTOM_EXPECTED_SQL = """DELETE FROM "users";
            |// Doing table users
            |BEGIN TRANSACTION;
            |INSERT INTO "users" ("id", "name", "age") VALUES (${IntId.one('vlad')}, 'Vladimir', 29);
            |""".stripMargin()

    def "should not insert any custom SQL by default"() {
        setup:
        def directory = unpackYamlToTempDirectory("default.yml")

        expect:
        JFixturesOld.sql99(directory as String).asString() == DEFAULT_EXPECTED_SQL

        cleanup:
        directory.toFile().deleteDir()
    }

    def "should insert custom SQL if present"() {
        setup:
        def directory = unpackYamlToTempDirectory("custom.yml")

        expect:
        JFixturesOld.sql99(directory as String).asString() == CUSTOM_EXPECTED_SQL

        cleanup:
        directory.toFile().deleteDir()
    }
}
