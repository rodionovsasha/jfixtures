package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class YamlConfigTest extends Specification implements YamlVirtualFolder {
    Path testFolderPath
    YamlConfig config

    void setup() {
        testFolderPath = unpackYamlToTempFolder("default.yml")
        config = new YamlConfig(testFolderPath.resolve("settings.yml"))
    }

    void cleanup() {
        testFolderPath.toFile().deleteDir()
    }

    def "reads setting from the root"() {
        expect:
        config.digValue("name").get() == "Vlad"
        config.digValue("age").get() == 29
    }

    def "reads settings from the tree"() {
        expect:
        config.digValue("music:kind:rock").get() == "rocks!"
    }

    def "reads settings by array of sections names"() {
        expect:
        config.digValue("music", "kind", "rock").get() == "rocks!"
    }

    def "sections can contain dots"() {
        expect:
        config.digValue(".every:.default:.id").get() == 10_000
    }

    def "returns empty optional if value was not found"() {
        expect:
        !config.digValue("who:is:this:guy").present
    }

    def "fails when the required section is an empty string"() {
        when:
        config.digValue("")

        then:
        thrown(IllegalArgumentException)
    }

    def "fails when the config file does not exist"() {
        when:
        new YamlConfig(notExistingPath())

        then:
        thrown(ConfigException)
    }

    def "fails if returning value is a node and not a value"() {
        when:
        config.digValue("music")

        then:
        thrown(ConfigException)
    }

    def "fails if requested section is a value not a section"() {
        when:
        config.digValue("name:vlad:first")

        then:
        thrown(ConfigException)
    }
}
