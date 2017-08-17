package com.github.vkorobkov.jfixtures.util

import spock.lang.Specification


class StringUtilTest extends Specification {

    def "dummy constructor test"() {
        expect:
        new StringUtil()
    }

    def "removePrefixes removed fists found prefix"() {
        expect:
        StringUtil.removePrefixes("todo:good_job", "todo:") == "good_job"
    }

    def "removePrefixes does not remove next matching prefixes"() {
        StringUtil.removePrefixes("todo:me:good_job", "todo:", "me:") == "me:good_job"
    }

    def "removePrefixes returns original string if not prefix matches"() {
        StringUtil.removePrefixes("todo:me:good_job", "you:", "me:") == "todo:me:good_job"
    }

    def "removePrefixes returns original string if not prefixes provided"() {
        StringUtil.removePrefixes("todo:me:good_job") == "todo:me:good_job"
    }
}
