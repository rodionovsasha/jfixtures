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

    def "should not have yml twin"() {
        expect:
        !YmlUtil.hasYamlTwin(testFolderPath.resolve("simple.yml"))
    }

    def "should not have yaml twin"() {
        expect:
        !YmlUtil.hasYamlTwin(testFolderPath.resolve("yaml_config.yaml"))
    }

    def "should have yaml twin"() {
        setup:
        def folder = unpackYamlToTempFolder("twins.yml")

        expect:
        YmlUtil.hasYamlTwin(folder.resolve("simple.yml"))

        cleanup:
        folder.toFile().deleteDir()
    }

    def "hasYamlTwin throws when neither yaml nor yml extension"() {
        setup:
        def folder = unpackYamlToTempFolder("txt.yml")

        when:
        YmlUtil.hasYamlTwin(folder.resolve("simple.txt"))

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Neither .yaml nor .yml file extension."

        cleanup:
        folder.toFile().deleteDir()
    }

    private load(String ymlFile) {
        YmlUtil.load(testFolderPath.resolve(ymlFile))
    }
}
