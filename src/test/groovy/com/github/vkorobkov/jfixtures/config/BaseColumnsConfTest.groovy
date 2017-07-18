package com.github.vkorobkov.jfixtures.config

import spock.lang.Specification

class BaseColumnsConfTest extends Specification {

    def SAMPLE_CONCERNS =  [
        has_version: [version: 1],
        has_date: [date: "12.02.1988"],
        has_new_date: [date: "02/12/1988"]
    ]
    def SAMPLE_CONF = [
        concerns: SAMPLE_CONCERNS,
        apply: [
            users_have_version: [to: "users", concerns: "has_version"],
            admins_have_version: [to: "/admin.*", concerns: "has_version"],

            comments_have_date: [to: "comments", concerns: "has_date"],
            comments_have_new_date: [to: "comments", concerns: "has_new_date"],
        ]
    ]

    def "extends a table with concern"() {
        expect:
        baseColumns(SAMPLE_CONF, "users") == [version: 1]
    }

    def "extends a table by regexp"() {
        expect:
        baseColumns(SAMPLE_CONF, "admin") == [version: 1]
        baseColumns(SAMPLE_CONF, "admin_comment") == [version: 1]
        baseColumns(SAMPLE_CONF, "admin_role") == [version: 1]
    }

    def "returns empty map when table does not match by name"() {
        expect:
        baseColumns(SAMPLE_CONF, "holly_crap").isEmpty()
    }

    def "returns empty map when table does not match by regexp"() {
        expect:
        baseColumns(SAMPLE_CONF, "/holly_crap").isEmpty()
    }

    def "the latest defined concern overrides columns of the previous one(s)"() {
        expect:
        baseColumns(SAMPLE_CONF, "comments") == [date: "02/12/1988"]
    }

    def "returns empty map if base_columns section is empty"() {
        baseColumns([:], "users").isEmpty()
    }

    def "any table regular expression applies to every table"() {
        when:
        def conf = [
            concerns: SAMPLE_CONCERNS,
            apply: [
                every_table_has_version: [to: "/.+", concerns: "has_version"]
            ]
        ]

        then:
        baseColumns(conf, "users") == [version: 1]
        baseColumns(conf, "admins") == [version: 1]
        baseColumns(conf, "comments") == [version: 1]
    }

    def "many concerns to many table as comma separated values"() {
        when:
        def conf = [
            concerns: SAMPLE_CONCERNS,
            apply: [
                people_are_versioned: [to: "users, admins", concerns: "has_version, has_date"]
            ]
        ]

        then:
        baseColumns(conf, "users") == [version: 1, date: "12.02.1988"]
        baseColumns(conf, "admins") == [version: 1, date: "12.02.1988"]

        and:
        baseColumns(conf, "comments").isEmpty()
    }

    def "many concerns to many table as arrays"() {
        when:
        def conf = [
            concerns: SAMPLE_CONCERNS,
            apply: [
                people_are_versioned: [to: ["users", "admins"], concerns: ["has_version", "has_date"]]
            ]
        ]

        then:
        baseColumns(conf, "users") == [version: 1, date: "12.02.1988"]
        baseColumns(conf, "admins") == [version: 1, date: "12.02.1988"]

        and:
        baseColumns(conf, "comments").isEmpty()
    }

    def "many concerns to many table as arrays of comma-separated values"() {
        when:
        def conf = [
            concerns: SAMPLE_CONCERNS,
            apply: [
                people_are_versioned: [to: ["users, admins"], concerns: ["has_version, has_date"]]
            ]
        ]

        then:
        baseColumns(conf, "users") == [version: 1, date: "12.02.1988"]
        baseColumns(conf, "admins") == [version: 1, date: "12.02.1988"]

        and:
        baseColumns(conf, "comments").isEmpty()
    }

    private static def baseColumns(Map config, String table) {
        config = [base_columns: config]
        new BaseColumnsConf(new YamlConfig(config)).baseColumns(table)
    }
}
