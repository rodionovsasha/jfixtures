package com.github.vkorobkov.jfixtures.util

import spock.lang.Specification

import java.nio.file.Paths


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
        expect:
        StringUtil.removePrefixes("todo:me:good_job", "todo:", "me:") == "me:good_job"
    }

    def "removePrefixes returns original string if not prefix matches"() {
        expect:
        StringUtil.removePrefixes("todo:me:good_job", "you:", "me:") == "todo:me:good_job"
    }

    def "removePrefixes returns original string if not prefixes provided"() {
        expect:
        StringUtil.removePrefixes("todo:me:good_job") == "todo:me:good_job"
    }

    def "#cutOffExtension returns file name without extension"() {
        expect:
        StringUtil.cutOffExtension(Paths.get("/home/user/config.yaml")) == Paths.get("/home/user/config")
    }

    def "#cutOffExtension returns file name for file without extension"() {
        expect:
        StringUtil.cutOffExtension(Paths.get("/home/user/config")) == Paths.get("/home/user/config")
    }

    def "#cutOffExtension returns file name without extension when file contains dots"() {
        expect:
        StringUtil.cutOffExtension(Paths.get("/home/user/my.config.yaml")) == Paths.get("/home/user/my.config")
    }

    def "#cutOffExtension returns file name without extension when directory contains dots"() {
        expect:
        StringUtil.cutOffExtension(Paths.get("/home/user/.m2/config.yaml")) == Paths.get("/home/user/.m2/config")
    }

    def "#cutOffExtension returns file when directory contains dots for file without extension"() {
        expect:
        StringUtil.cutOffExtension(Paths.get("/home/user/.m2/config")) == Paths.get("/home/user/.m2/config")
    }

    def "#cutOffExtension returns file name without extension without parent directory"() {
        expect:
        StringUtil.cutOffExtension(Paths.get("config.yaml")) == Paths.get("config")
    }
}
