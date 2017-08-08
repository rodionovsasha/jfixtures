package com.github.vkorobkov.jfixtures.config.structure.tables

import com.github.vkorobkov.jfixtures.config.yaml.Node
import spock.lang.Specification

class TablesTest extends Specification {
    def SAMPLE_CONFIG = [
            table_NOT_have_pk: [
                    applies_to: "friends",
                    pk: [generate: false],
            ],
            users_related_table_has_pk: [
                    applies_to: "users",
                    pk: [generate: true],
        ]
    ]

    def SAMPLE_CONFIG_REGEXP = [
            tables_NOT_have_pk: [
                    applies_to: "/.+",
                    pk: [generate: false],
            ],
            table_has_pk: [
                    applies_to: "users",
                    pk: [generate: true],
            ]
    ]

    def SAMPLE_CONFIG_WITH_CUSTOM_PK = [
            table_has_pk: [
                    applies_to: "users",
                    pk: [generate: true, column: "custom_id"],
            ]
    ]

    def "shouldAutoGeneratePk returns true by default"() {
        expect:
        shouldAutoGeneratePk(SAMPLE_CONFIG, "mates" )
    }

    def "should auto generate PK when generate flag has true value"() {
        expect:
        shouldAutoGeneratePk(SAMPLE_CONFIG, "users")
        shouldAutoGeneratePk(SAMPLE_CONFIG_REGEXP, "users")
    }

    def "should not auto generate PK when generate flag has false value"() {
        expect:
        !shouldAutoGeneratePk(SAMPLE_CONFIG, "friends")
        !shouldAutoGeneratePk(SAMPLE_CONFIG_REGEXP, "any_table")
    }

    def "should auto generate PK with default column name when generate flag does not exist"() {
        expect:
        getCustomColumnForPk(SAMPLE_CONFIG, "mates" ) == "id"
    }

    def "should auto generate PK with default column name when generate flag has true value"() {
        expect:
        getCustomColumnForPk(SAMPLE_CONFIG_REGEXP, "users" ) == "id"
    }


    def "should generate PK with custom column name"() {
        expect:
        getCustomColumnForPk(SAMPLE_CONFIG_WITH_CUSTOM_PK, "users" ) == "custom_id"
    }

    private static def shouldAutoGeneratePk(content, String name) {
        getTablesConfig(content, name).shouldAutoGeneratePk()
    }

    private static def getCustomColumnForPk(content, String name) {
        getTablesConfig(content, name).getPkColumnName()
    }

    private static def getTablesConfig(content, String name) {
        new Tables(Node.root(content), name)
    }
}
