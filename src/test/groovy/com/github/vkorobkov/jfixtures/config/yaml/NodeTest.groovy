package com.github.vkorobkov.jfixtures.config.yaml

import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

@Unroll
class NodeTest extends Specification {
    def "child() returns a node child if exist"() {
        when:
        def node = Node.root(copyright: 2017)

        then:
        node.child("copyright").required() == 2017
    }

    def "child() returns another Node instance"() {
        given:
        def root = Node.root(copyright: 2017)

        when:
        def child = root.child("copyright")

        then:
        !root.is(child)
    }

    def "chained child() returns nested sub node"() {
        when:
        def node = Node.root(copyright: [author: "vlad"])

        then:
        node.child("copyright").child("author").required() == "vlad"
    }

    def "child() returns empty node for non existing children"(Node node) {
        expect:
        !node.child("never go to the java conferences").optional().present

        where:
        node << differentRootNodes
    }

    def "chained child() returns empty node for non existing children"(Node node) {
        expect:
        !node.child("never go to the").child("java conferences").optional().present

        where:
        node << differentRootNodes
    }

    def "children() returns children nodes"() {
        given:
        def root = Node.root(name: "Vlad", age: 29)

        when:
        List<Node> children = root.children().collect(Collectors.toList())

        then:
        children[0].toString() == "name"
        children[0].required() == "Vlad"

        and:
        children[1].toString() == "age"
        children[1].required() == 29
    }

    def "children() is empty for values, arrays and empty nodes"(Node node) {
        expect:
        node.children().count() == 0

        where:
        node << nodesWithoutChildren
    }

    def "hasChildren() is true when a node has at least one child"(Node node) {
        expect:
        node.hasChildren()

        where:
        node << [
            Node.root(name: "Vlad", age: 29),
            Node.root(name: "Vlad"),
            Node.root(authors: [name: "Bill"]).child("authors"),
        ]
    }

    def "hasChildren() is false when a node has no children"(Node node) {
        expect:
        !node.hasChildren()

        where:
        node << nodesWithoutChildren
    }

    def "dig() returns a nested node"() {
        when:
        def node = Node.root(copyright: [author: "vlad"])

        then:
        node.dig("copyright", "author").required() == "vlad"
    }

    def "dig() with one level depth is equal to child()"() {
        when:
        def node = Node.root(copyright: 2017)

        then:
        node.dig("copyright").required() == 2017
    }

    def "required() returns a value if exists"(Node node) {
        expect:
        node.required() == "vlad"

        where:
        node << notEmptyNodes
    }

    def "required() throws an exception for empty node"(Node node) {
        when:
        node.required()

        then:
        thrown(NodeMissingException)

        where:
        node << emptyNodes
    }

    def "optinal() returns a value if exists"(Node node) {
        expect:
        node.optional().get() == "vlad"

        where:
        node << notEmptyNodes
    }

    def "optional() throws an exception for empty node"(Node node) {
        expect:
        !node.optional().present

        where:
        node << emptyNodes
    }


    def "toString() returns empty name for the root node"(Node node) {
        expect:
        node.toString().isEmpty()

        where:
        node << differentRootNodes
    }

    def "toString() returns subnode name"() {
        when:
        def node = Node.root(copyright: 2017, author: "vlad")

        then:
        node.child("copyright").toString() == "copyright"
        node.child("author").toString() == "author"
    }

    def "toString() returns nested subnode name"() {
        when:
        def node = Node.root(copyright: [author: "vlad"])

        then:
        node.child("copyright").child("author").toString() == "copyright:author"

        and:
        node.dig("copyright", "author").toString() == "copyright:author"
    }

    def "toString() returns valid subnode name even if subnode does not exist"() {
        when:
        def node = Node.emptyRoot()

        then:
        node.child("copyright").child("author").toString() == "copyright:author"

        and:
        node.dig("copyright", "author").toString() == "copyright:author"
    }

    def "::exists returns true if requested node exists"() {
        expect:
        Node.root(copyright: [author: "vlad"]).dig("copyright", "author").exists()
    }

    def "::exists returns true if node is empty"() {
        expect:
        Node.root(copyright: [:]).dig("copyright").exists()
    }

    def "::exists returns false if requested node does not exist"() {
        expect:
        !Node.root(copyright: [author: "vlad"]).dig("copyright", "year").exists()
    }

    def getNodesWithoutChildren() {
        [
            Node.emptyRoot(),
            Node.root("Value"),
            Node.root([:]),
            Node.root([1, 2, 3])
        ]
    }

    def getEmptyNodes() {
        [
            Node.emptyRoot(),
            Node.root(name: "vlad").child("lastName"),
            Node.root(authors: [name: "vlad"]).dig("authors", "age")
        ]
    }

    def getNotEmptyNodes() {
        [
            Node.root("vlad"),
            Node.root(name: "vlad").child("name"),
            Node.root(authors: [name: "vlad"]).dig("authors", "name")
        ]
    }

    def getDifferentRootNodes() {
        [
            Node.emptyRoot(),
            Node.root("Hello"),
            Node.root(refs: [1, 2,3], columns: ["id", "name"], tables: ["users", "roles"]),
            Node.root(["one", "two", "three", "fire!"])
        ]
    }
}
