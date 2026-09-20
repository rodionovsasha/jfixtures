package io.github.rodionovsasha.jfixtures

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class SqlHooksTest extends Specification {
    Path directory

    void setup() {
        directory = Files.createTempDirectory("jfixtures-hooks")
    }

    void cleanup() {
        directory.toFile().deleteDir()
    }

    def "runs conventional and configured hooks around all fixture statements"() {
        given:
        write("users.yml", "vlad:\n  name: Vlad\n")
        write(".conf.yml", """
before_all: ["file:sql/before-all.sql", "SET global_before"]
after_all: ["SET global_after", "file:sql/after-all.sql"]
tables:
  users:
    applies_to: users
    before_cleanup: "file:sql/before-cleanup.sql"
    before_inserts: "file:sql/before-inserts.sql"
    after_inserts: "file:sql/after-inserts.sql"
""")
        write("sql/before-all.sql", "SELECT 'before-all';\n")
        write("sql/after-all.sql", "SELECT 'after-all';\n")
        write("sql/before-cleanup.sql", "SELECT '${'$'}TABLE_NAME-cleanup';\n")
        write("sql/before-inserts.sql", "SELECT '${'$'}TABLE_NAME-inserts';\n")
        write("sql/after-inserts.sql", "SELECT '${'$'}TABLE_NAME-after';\n")
        write(".before/20-second.sql", "SELECT 'second';\n")
        write(".before/10-first.sql", "SELECT 'first';\n")
        write(".before/ignored.txt", "ignored")
        write(".after/10-final.sql", "SELECT 'final';\n")

        when:
        def sql = JFixtures.withConfig(directory.resolve(".conf.yml"))
                .load(directory)
                .compile()
                .toSql99()
                .toString()

        then:
        sql == expectedSql()
    }

    def "does not require conventional hook directories"() {
        given:
        write("users.yml", "vlad: {}\n")

        expect:
        JFixtures.noConfig().load(directory).compile().toSql99().toString().startsWith('DELETE FROM "users";')
    }

    private void write(String name, String contents) {
        Path path = directory.resolve(name)
        Files.createDirectories(path.parent)
        Files.writeString(path, contents)
    }

    private static String expectedSql() {
        """SELECT 'first';
SELECT 'second';
SELECT 'before-all';
SET global_before
SELECT 'users-cleanup';
DELETE FROM "users";
SELECT 'users-inserts';
INSERT INTO "users" ("id", "name") VALUES (${IntId.one("vlad")}, 'Vlad');
SELECT 'users-after';
SET global_after
SELECT 'after-all';
SELECT 'final';
"""
    }
}
