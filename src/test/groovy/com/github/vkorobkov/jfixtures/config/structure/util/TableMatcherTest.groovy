package com.github.vkorobkov.jfixtures.config.structure.util

import spock.lang.Specification

class TableMatcherTest extends Specification {

    def "matches by single table name"() {
        when:
        def matcher = new TableMatcherImpl("users")

        then:
        matcher.tableMatches("users")

        and:
        !matcher.tableMatches("admin")
    }

    def "matches by comma separated table names"() {
        when:
        def matcher = new TableMatcherImpl("users, admins")

        then:
        matcher.tableMatches("users")
        matcher.tableMatches("admins")

        and:
        !matcher.tableMatches("comments")
        !matcher.tableMatches("tickets")
    }

    def "matches by array of table name"() {
        when:
        def matcher = new TableMatcherImpl(["users", "admins"])

        then:
        matcher.tableMatches("users")
        matcher.tableMatches("admins")

        and:
        !matcher.tableMatches("comments")
        !matcher.tableMatches("tickets")
    }

    def "matches a regular expression"() {
        when:
        def matcher = new TableMatcherImpl("/user.+")

        then:
        matcher.tableMatches("users")
        matcher.tableMatches("user_comment")

        and:
        !matcher.tableMatches("comments")
        !matcher.tableMatches("tickets")
    }

    static class TableMatcherImpl implements TableMatcher {
        def tablesToMatch

        TableMatcherImpl(tablesToMatch) {
            this.tablesToMatch = tablesToMatch
        }

        @Override
        Object tablesToMatch() {
            tablesToMatch
        }
    }
}
