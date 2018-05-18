package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

class BeforeCleanupTest extends Specification implements YamlVirtualDirectory {
    def DEFAULT_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (${IntId.one('vlad')}, 'Vladimir', 29);
            |""".stripMargin()
    def CUSTOM_EXPECTED_SQL = """// Beginning of the table users
            |BEGIN TRANSACTION;
            |DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (${IntId.one('vlad')}, 'Vladimir', 29);
            |""".stripMargin()

    def "should not insert any custom SQL by default"() {
        setup:
        def directory = unpackYamlToTempDirectory("default.yml")

        when:
        def sql = JFixtures
            .noConfig()
            .loadDirectory(directory)
            .compile()
            .toSql99()
            .toString()

        then:
        sql == DEFAULT_EXPECTED_SQL

        cleanup:
        directory.toFile().deleteDir()
    }

    def "should insert custom SQL if present"() {
        setup:
        def directory = unpackYamlToTempDirectory("custom.yml")
        def conf = "${directory}/.conf.yml";

        when:
        def sql = JFixtures
            .withConfig(conf)
            .loadDirectory(directory)
            .compile()
            .toSql99()
            .toString()

        then:
        sql == CUSTOM_EXPECTED_SQL

        cleanup:
        directory.toFile().deleteDir()
    }
}
