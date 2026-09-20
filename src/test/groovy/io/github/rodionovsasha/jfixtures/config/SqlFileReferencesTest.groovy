package io.github.rodionovsasha.jfixtures.config

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class SqlFileReferencesTest extends Specification {
    Path directory

    void setup() {
        directory = Files.createTempDirectory("jfixtures-sql-files")
        Files.writeString(directory.resolve("hook.sql"), "SELECT 1;\n")
    }

    void cleanup() {
        directory.toFile().deleteDir()
    }

    def "resolves only hook file references recursively"() {
        given:
        def config = [
                before_all: ["file:hook.sql", "inline", 1],
                after_all: "file:hook.sql",
                tables: [users: [
                        applies_to: "users", before_cleanup: "file:hook.sql",
                        before_inserts: "file:hook.sql", after_inserts: "file:hook.sql"
                ]],
                unrelated: "file:hook.sql"
        ]

        when:
        def result = SqlFileReferences.resolve(config, directory)

        then:
        result.before_all == ["SELECT 1;", "inline", 1]
        result.after_all == "SELECT 1;"
        result.tables.users.before_cleanup == "SELECT 1;"
        result.tables.users.before_inserts == "SELECT 1;"
        result.tables.users.after_inserts == "SELECT 1;"
        result.unrelated == "file:hook.sql"
    }

    def "reports a missing referenced SQL file"() {
        when:
        SqlFileReferences.resolve([before_all: "file:missing.sql"], directory)

        then:
        thrown(java.nio.file.NoSuchFileException)
    }
}
