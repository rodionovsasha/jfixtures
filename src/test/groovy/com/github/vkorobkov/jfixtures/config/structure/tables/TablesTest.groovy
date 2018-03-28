package com.github.vkorobkov.jfixtures.config.structure.tables

import com.github.vkorobkov.jfixtures.config.yaml.Node
import com.github.vkorobkov.jfixtures.domain.Value
import spock.lang.Specification

class TablesTest extends Specification {
    def SAMPLE_CONFIG = [
            table_NOT_have_pk         : [
                    applies_to: "friends",
                    pk        : [generate: false],
            ],
            users_related_table_has_pk: [
                    applies_to: "users",
                    pk        : [generate: true],
            ]
    ]

    def SAMPLE_CONFIG_REGEXP = [
            tables_NOT_have_pk: [
                    applies_to: "/.+",
                    pk        : [generate: false],
            ],
            table_has_pk      : [
                    applies_to: "users",
                    pk        : [generate: true],
            ]
    ]

    def SAMPLE_CONFIG_WITH_CUSTOM_PK = [
            table_has_pk: [
                    applies_to: "users",
                    pk        : [generate: true, column: "custom_id"],
            ]
    ]

    def CLEAN_UP_CONFIG = [
            no_clean_up           : [
                    applies_to  : "users",
                    clean_method: "none",
            ],
            clean_up_with_delete  : [
                    applies_to  : "friends",
                    clean_method: "delete",
            ],
            clean_up_with_truncate: [
                    applies_to  : "mates",
                    clean_method: "truncate",
            ]
    ]

    def CLEAN_UP_CONFIG_REGEXP = [
            no_clean_up : [
                    applies_to  : "/.+",
                    clean_method: "none",
            ],
            table_has_pk: [
                    applies_to  : "users",
                    clean_method: "delete",
            ]
    ]

    def BEFORE_INSERTS_CONFIG = [
            transactional_users  : [
                    applies_to    : "users",
                    before_inserts: "BEGIN TRANSACTION;",
            ],
            transactional_friends: [
                    applies_to    : ["friends", "mates"],
                    before_inserts: ["// Doing table \$TABLE_NAME", "BEGIN TRANSACTION;"],
            ]
    ]

    def AFTER_INSERTS_CONFIG = [
            transactional_users  : [
                    applies_to   : "users",
                    after_inserts: "COMMIT TRANSACTION;",
            ],
            transactional_friends: [
                    applies_to   : ["friends", "mates"],
                    after_inserts: ["// Completed table \$TABLE_NAME", "COMMIT TRANSACTION;"],
            ]
    ]

    def BEFORE_CLEANUP_CONFIG = [
            transactional_users  : [
                    applies_to    : "users",
                    before_cleanup: "BEGIN TRANSACTION;",
            ],
            transactional_friends: [
                    applies_to    : ["friends", "mates"],
                    before_cleanup: ["// Beginning of the table \$TABLE_NAME", "BEGIN TRANSACTION;"],
            ]
    ]

    def "shouldAutoGeneratePk returns true by default"() {
        expect:
        shouldAutoGeneratePk(SAMPLE_CONFIG, "mates")
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
        getCustomColumnForPk(SAMPLE_CONFIG, "mates") == "id"
    }

    def "should auto generate PK with default column name when generate flag has true value"() {
        expect:
        getCustomColumnForPk(SAMPLE_CONFIG_REGEXP, "users") == "id"
    }


    def "should generate PK with custom column name"() {
        expect:
        getCustomColumnForPk(SAMPLE_CONFIG_WITH_CUSTOM_PK, "users") == "custom_id"
    }

    def "getCleanMethod returns 'delete' by default"() {
        expect:
        getCleanMethod(SAMPLE_CONFIG, "friends") == CleanMethod.DELETE
        getCleanMethod(SAMPLE_CONFIG, "users") == CleanMethod.DELETE
    }

    def "should return clean_method if set"() {
        expect:
        getCleanMethod(CLEAN_UP_CONFIG, "users") == CleanMethod.NONE
        getCleanMethod(CLEAN_UP_CONFIG, "friends") == CleanMethod.DELETE
        getCleanMethod(CLEAN_UP_CONFIG, "mates") == CleanMethod.TRUNCATE
    }

    def "should return clean_method if set for any table"() {
        expect:
        getCleanMethod(CLEAN_UP_CONFIG_REGEXP, "any_table") == CleanMethod.NONE
        getCleanMethod(CLEAN_UP_CONFIG_REGEXP, "users") == CleanMethod.DELETE
    }

    def "getBeforeInserts returns 'empty value' by default"() {
        expect:
        getBeforeInserts(SAMPLE_CONFIG, "users") == []
    }

    def "should return before_inserts if set"() {
        expect:
        getBeforeInserts(BEFORE_INSERTS_CONFIG, "users") == ["BEGIN TRANSACTION;"]
        getBeforeInserts(BEFORE_INSERTS_CONFIG, "friends") == ["// Doing table \$TABLE_NAME", "BEGIN TRANSACTION;"]
        getBeforeInserts(BEFORE_INSERTS_CONFIG, "mates") == ["// Doing table \$TABLE_NAME", "BEGIN TRANSACTION;"]
    }

    def "#getBeforeInserts allows commas in SQL statements"() {
        given:
        def config = [
            transactional_users  : [
                applies_to    : "users",
                before_inserts: "INSERT INTO LOG(time, event) VALUES (NOW(), 'before_table');"
            ]
        ]

        expect:
        getBeforeInserts(config, "users") == ["INSERT INTO LOG(time, event) VALUES (NOW(), 'before_table');"]
    }

    def "#getBeforeInserts concatenates sql fragments across applied rules"() {
        given:
        def config = [
            comment_on_users  : [
                applies_to    : "users",
                before_inserts: "-- Starting a new table"
            ],
            transactional_users: [
                applies_to    : "users",
                before_inserts: ["-- Starting transaction", "BEGIN TRANSACTION"]
            ]
        ]

        expect:
        getBeforeInserts(config, "users") == [
            "-- Starting a new table",
            "-- Starting transaction",
            "BEGIN TRANSACTION"
        ]
    }

    def "getAfterInserts returns 'empty value' by default"() {
        expect:
        getAfterInserts(SAMPLE_CONFIG, "users") == []
    }

    def "should return after_inserts if set"() {
        expect:
        getAfterInserts(AFTER_INSERTS_CONFIG, "users") == ["COMMIT TRANSACTION;"]
        getAfterInserts(AFTER_INSERTS_CONFIG, "friends") == ["// Completed table \$TABLE_NAME", "COMMIT TRANSACTION;"]
        getAfterInserts(AFTER_INSERTS_CONFIG, "mates") == ["// Completed table \$TABLE_NAME", "COMMIT TRANSACTION;"]
    }

    def "#getAfterInserts allows commas in SQL statements"() {
        given:
        def config = [
            transactional_users  : [
                applies_to    : "users",
                after_inserts: "INSERT INTO LOG(time, event) VALUES (NOW(), 'before_table');"
            ]
        ]

        expect:
        getAfterInserts(config, "users") == ["INSERT INTO LOG(time, event) VALUES (NOW(), 'before_table');"]
    }

    def "#getAfterInserts concatenates sql fragments across applied rules"() {
        given:
        def config = [
            comment_on_users  : [
                applies_to    : "users",
                after_inserts: "-- Table is done"
            ],
            transactional_users: [
                applies_to    : "users",
                after_inserts: ["-- Committing transaction", "COMMIT TRANSACTION"]
            ]
        ]

        expect:
        getAfterInserts(config, "users") == [
            "-- Table is done",
            "-- Committing transaction",
            "COMMIT TRANSACTION"
        ]
    }

    def "getBeforeCleanup returns 'empty value' by default"() {
        expect:
        getBeforeCleanup(SAMPLE_CONFIG, "users") == []
    }

    def "should return before_cleanup if set"() {
        expect:
        getBeforeCleanup(BEFORE_CLEANUP_CONFIG, "users") == ["BEGIN TRANSACTION;"]
        getBeforeCleanup(BEFORE_CLEANUP_CONFIG, "friends") == ["// Beginning of the table \$TABLE_NAME", "BEGIN TRANSACTION;"]
        getBeforeCleanup(BEFORE_CLEANUP_CONFIG, "mates") == ["// Beginning of the table \$TABLE_NAME", "BEGIN TRANSACTION;"]
    }

    def "#getBeforeCleanup allows commas in SQL statements"() {
        given:
        def config = [
            transactional_users  : [
                applies_to    : "users",
                before_cleanup: "INSERT INTO LOG(time, event) VALUES (NOW(), 'before_table');"
            ]
        ]

        expect:
        getBeforeCleanup(config, "users") == ["INSERT INTO LOG(time, event) VALUES (NOW(), 'before_table');"]
    }

    def "#getBeforeCleanup concatenates sql fragments across applied rules"() {
        given:
        def config = [
            comment_on_users  : [
                applies_to    : "users",
                before_cleanup: "-- Starting a new table"
            ],
            transactional_users: [
                applies_to    : "users",
                before_cleanup: ["-- Starting transaction", "BEGIN TRANSACTION"]
            ]
        ]

        expect:
        getBeforeCleanup(config, "users") == [
            "-- Starting a new table",
            "-- Starting transaction",
            "BEGIN TRANSACTION"
        ]
    }

    def "#getDefaultColumns returns empty map if tables is not matched by the rule"() {
        expect:
        getTablesConfig([:], "users").defaultColumns.isEmpty()
    }

    def "#getDefaultColumns returns a map with base columns"() {
        given:
        def config = [
                base_version: [
                        applies_to: "users",
                        default_columns: [version: 1]
                ]
        ]

        when:
        def result = getTablesConfig(config, "users").defaultColumns

        then:
        result == [version: Value.of(1)]
    }

    def "#getDefaultColumns merges accross the matching rules"() {
        given:
        def config = [
                base_version: [
                        applies_to: "users",
                        default_columns: [version: 1]
                ],

                base_date: [
                        applies_to: "users",
                        default_columns: [version: 2, date: 'NOW()']
                ]
        ]

        when:
        def result = getTablesConfig(config, "users").defaultColumns

        then:
        result == [version: Value.of(2), date: Value.of("NOW()")]
    }

    private static def shouldAutoGeneratePk(content, String name) {
        getTablesConfig(content, name).shouldAutoGeneratePk()
    }

    private static def getCustomColumnForPk(content, String name) {
        getTablesConfig(content, name).getPkColumnName()
    }

    private static def getCleanMethod(content, String name) {
        getTablesConfig(content, name).getCleanMethod()
    }

    private static def getBeforeInserts(content, String name) {
        getTablesConfig(content, name).getBeforeInserts()
    }

    private static def getAfterInserts(content, String name) {
        getTablesConfig(content, name).getAfterInserts()
    }

    private static def getBeforeCleanup(content, String name) {
        getTablesConfig(content, name).getBeforeCleanup()
    }

    private static def getTablesConfig(content, String name) {
        new Tables(Node.root(content), name)
    }
}
