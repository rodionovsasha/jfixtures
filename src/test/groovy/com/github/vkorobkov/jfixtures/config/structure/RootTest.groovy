package com.github.vkorobkov.jfixtures.config.structure

import com.github.vkorobkov.jfixtures.config.yaml.Node
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RootTest extends Specification {
    def "::empty creates a new config instance"() {
        expect:
        Root.empty()
    }

    def "::ofProfile creates config on the top of the root node if requested profile does not exist"() {
        given:
        def config = [
                refs: [users: [role_id: "roles"]],
                profiles: [unit: [
                        refs: [users: [role_id: "unit_roles"]],
                ]]
        ]

        when:
        def root = root(config, "integration")

        then:
        root.referredTable("users", "role_id").get() == "roles"
    }

    def "::ofProfile creates config with specified profile"() {
        given:
        def config = [
                refs: [users: [role_id: "roles"]],
                profiles: [unit: [
                        refs: [users: [role_id: "unit_roles"]],
                ]]
        ]

        when:
        def root = root(config, "unit")

        then:
        root.referredTable("users", "role_id").get() == "unit_roles"
    }

    def "::referredTable() positive case"() {
        when:
        def root = root(refs: [users: [role_id: "roles"]])

        then:
        root.referredTable("users", "role_id").get() == "roles"
    }

    def "::referredTable() returns empty optional when does not match"(table, column) {
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

    def "::referredTable() returns empty optional if refs section does not exist"() {
        expect:
        !root([:]).referredTable("users", "role_id").present
    }

    def root(content, profile = "default") {
        Root.ofProfile(Node.root(content), profile)
    }
}
