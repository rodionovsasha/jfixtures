package com.github.vkorobkov.jfixtures.config.structure.columns

import com.github.vkorobkov.jfixtures.config.yaml.Node
import spock.lang.Specification

class ColumnsTest extends Specification {

    def "returns base columns for a table"() {
        when:
        def data = [
            concerns: [
                has_version: [version: 1],
                has_date: [cr_date: "NOW"]
            ],
            apply: [
                users_has_version: [
                    to: "users",
                    concerns: "has_version, has_date"
                ],
                tickets_has_version: [
                    to: "tickets",
                    concerns: "has_version"
                ]
            ]
        ]

        then:
        сolumns(data, "users") == [version: 1, cr_date: "NOW"]

        and:
        сolumns(data, "tickets") == [version: 1]
    }

    def "applies concerns from many apply blocks one by one"() {
        when:
        def data = [
            concerns: [
                has_version: [version: 1],
                has_date: [cr_date: "NOW"]
            ],
            apply: [
                users_has_version: [
                    to: "users",
                    concerns: "has_version"
                ],
                tickets_has_date: [
                    to: "users",
                    concerns: "has_date"
                ]
            ]
        ]

        then:
        сolumns(data, "users") == [version: 1, cr_date: "NOW"]
    }

    def "latest apply block overrides previous on conflict"() {
        when:
        def data = [
            concerns: [
                has_version: [version: 1, versioned: true],
                next_version: [version: 2]
            ],
            apply: [
                users_has_version: [
                    to: "users",
                    concerns: "has_version"
                ],
                users_has_new_version: [
                    to: "users",
                    concerns: "next_version"
                ]
            ]
        ]

        then:
        сolumns(data, "users") == [version: 2, versioned: true]
    }

    def "returns nothing if tables do not match"() {
        when:
        def data = [
            concerns: [
                has_version: [version: 1, versioned: true]
            ],
            apply: [
                users_has_version: [
                    to: "users",
                    concerns: "has_version"
                ]
            ]
        ]

        then:
        сolumns(data, "people").isEmpty()
    }

    private static def сolumns(content, table) {
        new Columns(Node.root(content)).forTable(table)
    }
}
