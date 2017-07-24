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

    def "should auto generate PK when autoGeneratePk flag does not exist"() {
        expect:
        shouldAutoGeneratePk(SAMPLE_CONFIG, "mates" )
    }

    def "should auto generate PK when autoGeneratePk flag has true value"() {
        expect:
        shouldAutoGeneratePk(SAMPLE_CONFIG, "users")
        shouldAutoGeneratePk(SAMPLE_CONFIG_REGEXP, "users")
    }

    def "should not auto generate PK when autoGeneratePk flag has false value"() {
        expect:
        !shouldAutoGeneratePk(SAMPLE_CONFIG, "friends")
        !shouldAutoGeneratePk(SAMPLE_CONFIG_REGEXP, "any_table")
    }

    private static def shouldAutoGeneratePk(Map config, String table) {
        config = [tables: config]
        new TablesConfig(new YamlConfig(config)).shouldAutoGeneratePk(table)
    }
}
