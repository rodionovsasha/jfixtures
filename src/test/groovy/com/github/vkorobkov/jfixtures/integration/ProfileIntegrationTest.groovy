package com.github.vkorobkov.jfixtures.integration

import com.github.vkorobkov.jfixtures.JFixtures
import com.github.vkorobkov.jfixtures.domain.Row
import com.github.vkorobkov.jfixtures.domain.Table
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

import java.nio.file.Path

class ProfileIntegrationTest extends Specification implements YamlVirtualDirectory {
    Path tmpDirectoryPath

    static DELETE_SQL = """DELETE FROM "users";\n"""
    static INSERT_SQL = """INSERT INTO "users" ("id", "name", "age") VALUES (1, 'Vlad', 30);\n"""

    def data = [
            Table.of("users",
                    Row.of("vlad").columns(id: 1, name: "Vlad", age: 30)
            )
    ]

    void setup() {
        tmpDirectoryPath = unpackYamlToTempDirectory("default.yml")
    }

    void cleanup() {
        tmpDirectoryPath.toFile().deleteDir()
    }

    def "the default profile cleans table up before the insertions"() {
        expect:
        subject() == DELETE_SQL + INSERT_SQL
    }

    def "special profiles turns cleaning the tables off"() {
        expect:
        subject("no_cleanup") == INSERT_SQL
    }

    def subject(profile = "default") {
        JFixtures
                .withConfig("${tmpDirectoryPath}/.conf.yml")
                .withProfile(profile)
                .addTables(data)
                .compile()
                .toSql99()
                .toString()
    }
}
