package com.github.vkorobkov.jfixtures.config.structure

import com.github.vkorobkov.jfixtures.config.yaml.Node
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RootTest extends Specification {
    def "referredTable() positive case"() {
        when:
        def root = root(refs: [users: [role_id: "roles"]])

        then:
        root.referredTable("users", "role_id").get() == "roles"
    }

    def "referredTable() returns empty optional when does not match"(table, column) {
        when:
        def root = root(refs: [users: [role_id: "roles"]])

        then:
        !root.referredTable(table, column).present

        where:
        table   | column
        "sr"    | "roles"
        "users" | "rls"
        "sr"    | "rls"
    }

    def "referredTable() returns empty optional if refs section does not exist"() {
        expect:
        !root([:]).referredTable("users", "role_id").present
    }

    def root(content) {
        new Root(Node.root(content))
    }
}
