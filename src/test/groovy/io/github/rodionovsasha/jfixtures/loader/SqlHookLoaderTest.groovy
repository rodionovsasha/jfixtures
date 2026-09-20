package io.github.rodionovsasha.jfixtures.loader

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission

class SqlHookLoaderTest extends Specification {
    Path directory

    void setup() {
        directory = Files.createTempDirectory("jfixtures-hook-loader")
    }

    void cleanup() {
        directory.toFile().deleteDir()
    }

    def "ignores non-regular and non-SQL entries"() {
        given:
        Files.createDirectories(directory.resolve(".before/nested"))
        Files.writeString(directory.resolve(".before/ignored.txt"), "ignored")
        Files.writeString(directory.resolve(".before/20-second.sql"), "second\n")
        Files.writeString(directory.resolve(".before/10-first.sql"), "first\n")

        expect:
        SqlHookLoader.load(directory).before() == ["first", "second"]
        SqlHookLoader.load(directory).after().isEmpty()
    }

    def "reports an unreadable SQL hook"() {
        given:
        Path hook = directory.resolve(".before/secret.sql")
        Files.createDirectories(hook.parent)
        Files.writeString(hook, "secret")
        Files.setPosixFilePermissions(hook, [] as Set<PosixFilePermission>)

        when:
        SqlHookLoader.load(directory)

        then:
        thrown(java.nio.file.AccessDeniedException)
    }
}
