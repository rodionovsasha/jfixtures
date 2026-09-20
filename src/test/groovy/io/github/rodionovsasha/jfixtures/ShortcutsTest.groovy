package io.github.rodionovsasha.jfixtures

import spock.lang.Specification

import java.nio.file.Files

class ShortcutsTest extends Specification {
    def "::Str.sql99 renders a fixture directory"() {
        given:
        def directory = Files.createTempDirectory("jfixtures-shortcut")
        Files.writeString(directory.resolve("users.yml"), "vlad:\n  name: Vlad\n")

        expect:
        Shortcuts.Str.sql99(directory.toString()).contains('INSERT INTO "users"')

        cleanup:
        directory.toFile().deleteDir()
    }

    def "::Str.sql99 uses the supplied configuration"() {
        given:
        def directory = Files.createTempDirectory("jfixtures-shortcut")
        Files.writeString(directory.resolve("users.yml"), "vlad:\n  name: Vlad\n")
        def config = directory.resolve(".config.yml")
        Files.writeString(config, "tables:\n  no_cleanup:\n    applies_to: users\n    clean_method: none\n")

        expect:
        !Shortcuts.Str.sql99(directory.toString(), config.toString()).contains('DELETE FROM "users"')

        cleanup:
        directory.toFile().deleteDir()
    }
}
