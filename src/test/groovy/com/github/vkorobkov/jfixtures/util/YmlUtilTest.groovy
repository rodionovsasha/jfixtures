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

    private load(String ymlFile) {
        YmlUtil.load(testFolderPath.resolve(ymlFile))
    }
}
