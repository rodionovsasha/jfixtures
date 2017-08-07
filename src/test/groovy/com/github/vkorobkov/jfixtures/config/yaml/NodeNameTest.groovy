package com.github.vkorobkov.jfixtures.config.yaml

import spock.lang.Specification

class NodeNameTest extends Specification {
    def "should create empty root node"() {
        when:
        def root = NodeName.root()

        then:
        root.toString().isEmpty()

        and:
        root.root
    }

    def "should create a subnode"() {
        given:
        def root = NodeName.root()

        when:
        def child = root.sub("items")

        then:
        child.toString() == "items"

        and:
        !child.root
    }

    def "should create subnodes"() {
        given:
        def root = NodeName.root()

        when:
        def child = root.sub("items", "human")

        then:
        child.toString() == "items:human"

        and:
        !child.root
    }

    def "subnode does not mutate parent node"() {
        given:
        def root = NodeName.root()

        when:
        root.sub("items")

        then:
        root.toString().isEmpty()

        and:
        root.root
    }

    def "sub returns another instance other than root"() {
        given:
        def root = NodeName.root()

        when:
        def child = root.sub("items", "human")

        then:
        !root.is(child)
    }
}
