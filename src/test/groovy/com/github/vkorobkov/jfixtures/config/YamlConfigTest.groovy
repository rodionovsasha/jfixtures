package com.github.vkorobkov.jfixtures.config

import spock.lang.Specification

class YamlConfigTest extends Specification {
    YamlConfig config

    void setup() {
        config = new YamlConfig([
            name: "Vlad",
            age: 29,
            music: [
                kind: [
                    rock: "rocks!"
                ]
            ],
            ".every": [
                ".default": [
                    ".id": 10000
                ]
            ]
        ])
    }

    def "reads setting from the root"() {
        expect:
        config.digValue("name").get() == "Vlad"
        config.digValue("age").get() == 29
    }

    def "returns required values without Optional wrapper"() {
        expect:
        config.digRequiredValue("name") == "Vlad"
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

    def "throws if required values was not found"() {
        given:
        def section = "who:is:this:guy"

        when:
        config.digRequiredValue(section)

        then:
        def e = thrown(ConfigException)
        e.message == "Section [$section] is required but absent in the configuration file"
    }

    def "fails when the required section is an empty string"() {
        when:
        config.digValue("")

        then:
        thrown(IllegalArgumentException)
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

    def "returns existing node"() {
        expect:
        config.digNode("music:kind").get() == [rock: "rocks!"]
    }

    def "returns existing required node without Optional wrapper"() {
        expect:
        config.digRequiredNode("music:kind") == [rock: "rocks!"]
    }

    def "returns empty optional if node does not exist"() {
        expect:
        !config.digNode("music:from:the:moon").present
    }

    def "throws if required node was not found"() {
        given:
        def section = "music:from:the:moon"

        when:
        config.digRequiredNode(section)

        then:
        def e = thrown(ConfigException)
        e.message == "Section [$section] is required but absent in the configuration file"
    }

    def "gets value by array of sections"() {
        expect:
        config.digValue("music", "kind", "rock").get() == "rocks!"
    }

    def "gets node by array of sections"() {
        expect:
        config.digNode("music", "kind").get() == [rock: "rocks!"]
    }

    def "throws when we dig for a node but it is a value"() {
        when:
        config.digNode("name")

        then:
        thrown(ConfigException)
    }

    def "list values recursively visits children nodes and arrays keeping the order"() {
        given:
        def section = [
            "cs": [
                "langualges": ["Java", "Ruby", "Python"],
                "algorithm": "binary",
            ],
            "people": ["Vlad", "Superman"]
        ]

        when:
        def result = []
        config.visitValuesRecursively(section, { result.add(it) })

        then:
        result == ["Java", "Ruby", "Python", "binary", "Vlad", "Superman"]
    }

    def "list values recursively returns a single value if the root node is value itself"() {
        given:
        def section = "Vlad"

        when:
        def result = []
        config.visitValuesRecursively(section, { result.add(it) })

        then:
        result == ["Vlad"]
    }

    def "list values recursively handles an array root node"() {
        given:
        def section = ["one", "two", "three"]

        when:
        def result = []
        config.visitValuesRecursively(section, { result.add(it) })

        then:
        result == ["one", "two", "three"]
    }
}
