package com.github.vkorobkov.jfixtures.config

import spock.lang.Specification

class TablesConfigTest extends Specification {
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

    def "should auto generate PK when generate flag does not exist"() {
        expect:
        shouldAutoGeneratePk(SAMPLE_CONFIG, "mates" )
        getCustomColumnForPk(SAMPLE_CONFIG, "mates" ) == "id"
    }

    def "should auto generate PK when generate flag has true value"() {
        expect:
        shouldAutoGeneratePk(SAMPLE_CONFIG, "users")
        shouldAutoGeneratePk(SAMPLE_CONFIG_REGEXP, "users")
        getCustomColumnForPk(SAMPLE_CONFIG_REGEXP, "users" ) == "id"
    }

    def "should generate PK with custom column name"() {
        expect:
        getCustomColumnForPk(SAMPLE_CONFIG_WITH_CUSTOM_PK, "users" ) == "custom_id"
    }

    def "should not auto generate PK when generate flag has false value"() {
        expect:
        !shouldAutoGeneratePk(SAMPLE_CONFIG, "friends")
        !shouldAutoGeneratePk(SAMPLE_CONFIG_REGEXP, "any_table")
    }

    private static def shouldAutoGeneratePk(Map config, String table) {
        getTablesConfig(config).shouldAutoGeneratePk(table)
    }

    private static def getCustomColumnForPk(Map config, String table) {
        getTablesConfig(config).getCustomColumnForPk(table)
    }

    private static def getTablesConfig(Map config) {
        config = [tables: config]
        new TablesConfig(new YamlConfig(config))
    }
}
