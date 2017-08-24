package com.github.vkorobkov.jfixtures.util

import spock.lang.Specification


class SqlUtilTest extends Specification {

    def "dummy constructor test"() {
        expect:
        new SqlUtil()
    }

    def "surrounds string with postfix and suffix"() {
        expect:
        SqlUtil.surround("hello", "|") == "|hello|"
    }

    def "surrounds an empty string"() {
        expect:
        SqlUtil.surround("", "|") == "||"
    }

    def "surround does not change string if surround_with is empty"() {
        expect:
        SqlUtil.surround("hello", "") == "hello"
    }

    def "surround fails with NPE when string is null"() {
        when:
        SqlUtil.surround(null, "|")

        then:
        thrown(NullPointerException)
    }

    def "surround fails with NPE when surround_with value is null"() {
        when:
        SqlUtil.surround("hello", null)

        then:
        thrown(NullPointerException)
    }

    def "escapeString replace all single quotes and surrounds with single quotes"() {
        expect:
        SqlUtil.escapeString("Vlad's code and Vlad's bug") == "'Vlad''s code and Vlad''s bug'"
    }

    def "escapeString fails with NPE when the input string is null"() {
        when:
        SqlUtil.escapeString(null)

        then:
        thrown(NullPointerException)
    }

    def "surrounds string with start prefix and end suffix"() {
        expect:
        SqlUtil.surround("hello", "[", "]") == "[hello]"
    }
}
