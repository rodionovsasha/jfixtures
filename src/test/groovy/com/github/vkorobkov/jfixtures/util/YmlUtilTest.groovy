package com.github.vkorobkov.jfixtures.util

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

import java.nio.file.NoSuchFileException
import java.nio.file.Path

class YmlUtilTest extends Specification implements YamlVirtualDirectory {

    Path testDirectoryPath

    void setup() {
        testDirectoryPath = unpackYamlToTempDirectory("default.yml")
    }

    void cleanup() {
        testDirectoryPath.toFile().deleteDir()
    }

    def "dummy constructor test"() {
        expect:
        new YmlUtil()
    }

    def "loads simple yaml"() {
        when:
        def yaml = load("simple.yml")

        then:
        yaml.name == "Vlad"
        yaml.age == 29
    }

    def "empty YML returns empty map"() {
        when:
        def yaml = load("empty.yml")

        then:
        yaml.isEmpty()
    }

    def "#hasYamlTwin does not have twin for .yml file"() {
        expect:
        !YmlUtil.hasTwin(testDirectoryPath.resolve("simple.yml"))
    }

    def "#hasYamlTwin does not have twin for .yaml file"() {
        expect:
        !YmlUtil.hasTwin(testDirectoryPath.resolve("yaml_config.yaml"))
    }

    def "#hasYamlTwin has twin for .yml file"() {
        setup:
        def directory = unpackYamlToTempDirectory("twins.yml")

        expect:
        YmlUtil.hasTwin(directory.resolve("user.yml"))

        cleanup:
        directory.toFile().deleteDir()
    }

    def "#hasYamlTwin has twin for .yaml file"() {
        setup:
        def directory = unpackYamlToTempDirectory("twins.yml")

        expect:
        YmlUtil.hasTwin(directory.resolve("admin.yaml"))

        cleanup:
        directory.toFile().deleteDir()
    }

    def "#hasYamlTwin throws when neither yaml nor yml extension"() {
        setup:
        def directory = unpackYamlToTempDirectory("txt.yml")

        when:
        YmlUtil.hasTwin(directory.resolve("simple.txt"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("it's yaml/yml twin does exist")

        cleanup:
        directory.toFile().deleteDir()
    }

    def "#hasYamlTwin throws when file without extension"() {
        setup:
        def directory = unpackYamlToTempDirectory("txt.yml")

        when:
        YmlUtil.hasTwin(directory.resolve("config_without_extension"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("it's yaml/yml twin does exist")

        cleanup:
        directory.toFile().deleteDir()
    }

    def "#hasYamlTwin throws for directory"() {
        setup:
        def directory = unpackYamlToTempDirectory("config_with_directory.yml")

        when:
        YmlUtil.hasTwin(directory.resolve("admin"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("admin nor it's yaml/yml twin does exist")

        cleanup:
        directory.toFile().deleteDir()
    }

    def "#hasYamlTwin does not have twin for directory"() {
        when:
        def directory = unpackYamlToTempDirectory("config_with_directory.yml")

        then:
        !YmlUtil.hasTwin(directory.resolve("config.yaml"))

        cleanup:
        directory.toFile().deleteDir()
    }

    def "#load throws an exception if file does not exist"() {
        when:
        load(notExistingPath().toString())

        then:
        thrown(NoSuchFileException)
    }

    private load(String ymlFile) {
        YmlUtil.load(testDirectoryPath.resolve(ymlFile))
    }
}
