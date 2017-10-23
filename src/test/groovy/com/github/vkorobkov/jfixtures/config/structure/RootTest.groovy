package com.github.vkorobkov.jfixtures.config.structure

import com.github.vkorobkov.jfixtures.config.yaml.Node
import spock.lang.Specification

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

    def "columns() returns base columns for a specific table"() {
        when:
        def data = [
                concerns: [has_version: [version: 1]],
                apply   : [
                        users_table_has_version: [to: "users", concerns: "has_version"]
                ]
        ]

        then:
        root(columns: data).columns().forTable("users") == [version: 1]
    }

    def root(content) {
        new Root(Node.root(content))
    }
}
