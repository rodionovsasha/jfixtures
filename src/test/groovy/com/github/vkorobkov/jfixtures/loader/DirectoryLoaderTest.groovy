package com.github.vkorobkov.jfixtures.loader

import com.github.vkorobkov.jfixtures.domain.Table
import com.github.vkorobkov.jfixtures.domain.Value
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

import java.nio.file.NoSuchFileException

class DirectoryLoaderTest extends Specification implements YamlVirtualDirectory {

    def "loads big fixtures tree"() {
        when:
        def fixtures = load("fixtures_tree.yml")

        then:
        fixtures.size() == 11
        def names = fixtures*.name

        and:
        names.containsAll(["usage_log", "sessions"])

        and:
        names.containsAll(["admin.users", "admin.permissions", "admin.registrations"])

        and:
        names.containsAll(["content.tickets", "content.comments", "content.filters"])

        and:
        names.containsAll(["search.comments", "search.descriptions", "search.wikis"])
    }

    def "fails if fixture name contains a dot"() {
        when:
        load("fixture_with_dot.yml")

        then:
        LoaderException exception = thrown()
        exception.message.startsWith("Do not use dots in file names. Use nested directorys instead. Wrong fixture:")
    }

    def "fails with IOException if directory with fixtures does not exist"() {
        given:
        def path = notExistingPath() as String

        when:
        new DirectoryLoader(path).load()

        then:
        def exception = thrown(LoaderException)
        exception.cause instanceof NoSuchFileException
    }

    def "returns no fixtures if source directory is empty"() {
        when:
        def fixtures = load("empty_directory.yml")

        then:
        fixtures.isEmpty()
    }

    def "it is possible to load fixture's rows"() {
        when:
        def fixtures = load("fixtures_tree_with_content.yml")

        then:
        fixtures.size() == 11
        def users = fixtures.find { it.name == "admin.users"}.rows

        and:
        def vlad = users[0]
        vlad.name == "vlad"
        vlad.columns.first_name == Value.of("Vladimir")
        vlad.columns.age == Value.of(29)
        vlad.columns.sex == Value.of("man")

        and:
        def dima = users[1]
        dima.name == "diman"
        dima.columns.first_name == Value.of("Dmitry")
        dima.columns.age == Value.of(28)
        dima.columns.sex == Value.of("man")
    }

    def "short syntax for row content is loaded well"() {
        when:
        def fixtures = load("fixture_with_short_syntax.yml")

        then:
        def users = fixtures.find { it.name == "users"}.rows

        and:
        with(users[0]) {
            name == "vlad"
            with(columns) {
                first_name == Value.of("Vladimir")
                age == Value.of(29)
                sex == Value.of("man")
            }
        }

        and:
        with(users[1]) {
            name == "diman"
            with(columns) {
                first_name == Value.of("Dmitry")
                age == Value.of(28)
                sex == Value.of("man")
            }
        }
    }

    def "#load consumes fixture with .yaml extension"() {
        when:
        def fixtures = load("yaml_config.yaml")

        then:
        def users = fixtures.find { it.name == "users"}.rows

        and:
        def vlad = users[0]
        vlad.name == "vlad"
        vlad.columns.first_name == Value.of("Vladimir")
        vlad.columns.age == Value.of(29)

        and:
        def dima = users[1]
        dima.name == "diman"
        dima.columns.first_name == Value.of("Dmitry")
        dima.columns.age == Value.of(28)
    }

    def "#load throws when a yaml fixture has a twin"() {
        when:
        load("yaml_yml.yaml")

        then:
        def e = thrown(LoaderException)
        e.message =~ /File (.*) exists with both extensions\(yaml\/yml\)/
    }

    def "#load throws when a fixture's parent directory name contains dots"() {
        when:
        load("fixture_parent_directory_with_dots.yml")

        then:
        LoaderException exception = thrown()
        exception.message.startsWith("Do not use dots in directory names. Wrong fixture directory: parent.directory.with.dots")
    }

    def "#load throws when a fixture's ancestor directory name contains dots"() {
        when:
        load("fixture_ancestor_directory_with_dots.yml")

        then:
        LoaderException exception = thrown()
        exception.message.startsWith("Do not use dots in directory names. Wrong fixture directory: ancestor.directory.with.dots/parentDirectory")
    }

    Collection<Table> load(String ymlFile) {
        def path = unpackYamlToTempDirectory(ymlFile) as String
        new DirectoryLoader(path).load()
    }
}
