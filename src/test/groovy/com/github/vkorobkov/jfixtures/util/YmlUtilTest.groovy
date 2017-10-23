package com.github.vkorobkov.jfixtures.util

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class YmlUtilTest extends Specification implements YamlVirtualFolder {

    Path testFolderPath

    void setup() {
        testFolderPath = unpackYamlToTempFolder("default.yml")
    }

    void cleanup() {
        testFolderPath.toFile().deleteDir()
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
        !YmlUtil.hasTwin(testFolderPath.resolve("simple.yml"))
    }

    def "#hasYamlTwin does not have twin for .yaml file"() {
        expect:
        !YmlUtil.hasTwin(testFolderPath.resolve("yaml_config.yaml"))
    }

    def "#hasYamlTwin has twin for .yml file"() {
        setup:
        def folder = unpackYamlToTempFolder("twins.yml")

        expect:
        YmlUtil.hasTwin(folder.resolve("user.yml"))

        cleanup:
        folder.toFile().deleteDir()
    }

    def "#hasYamlTwin has twin for .yaml file"() {
        setup:
        def folder = unpackYamlToTempFolder("twins.yml")

        expect:
        YmlUtil.hasTwin(folder.resolve("admin.yaml"))

        cleanup:
        folder.toFile().deleteDir()
    }

    def "#hasYamlTwin throws when neither yaml nor yml extension"() {
        setup:
        def folder = unpackYamlToTempFolder("txt.yml")

        when:
        YmlUtil.hasTwin(folder.resolve("simple.txt"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("Neither .yaml nor .yml file extension:")

        cleanup:
        folder.toFile().deleteDir()
    }

    def "#hasYamlTwin throws when file without extension"() {
        setup:
        def folder = unpackYamlToTempFolder("txt.yml")

        when:
        YmlUtil.hasTwin(folder.resolve("config_without_extension"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("Neither .yaml nor .yml file extension:")

        cleanup:
        folder.toFile().deleteDir()
    }

    def "#hasYamlTwin throws for directory"() {
        setup:
        def folder = unpackYamlToTempFolder("config_with_directory.yml")

        when:
        YmlUtil.hasTwin(folder.resolve("admin"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains("admin is directory and cannot be used as a config file")

        cleanup:
        folder.toFile().deleteDir()
    }

    private load(String ymlFile) {
        YmlUtil.load(testFolderPath.resolve(ymlFile))
    }
}
