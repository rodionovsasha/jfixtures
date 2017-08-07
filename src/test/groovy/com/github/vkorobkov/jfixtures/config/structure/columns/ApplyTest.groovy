package com.github.vkorobkov.jfixtures.config.structure.columns

import com.github.vkorobkov.jfixtures.config.yaml.Node
import spock.lang.Specification

import java.util.stream.Collectors

class ApplyTest extends Specification {

    def "returns concerns for a table"() {
        when:
        def apply = createApply(to: "admin, user", concerns: "has_version, has_cr_date")

        then:
        getConcerns(apply, "admin") == ["has_version", "has_cr_date"]
        getConcerns(apply, "user") == ["has_version", "has_cr_date"]
    }

    def "duplicated concerns are not getting removed and the order is saved"() {
        when:
        def apply = createApply(to: "admin", concerns: "has_version, has_cr_date, has_version")

        then:
        getConcerns(apply, "admin") == ["has_version", "has_cr_date", "has_version"]
    }

    def "returns empty stream if table does not match"() {
        expect:
        getConcerns(createApply(to: "admin", concerns: "has_version"), "users").empty
    }

    def "concerns are getting extracted from nested arrays"() {
        when:
        def apply = createApply(to: "admin", concerns: ["one", ["two, three", "four", ["five", "six", "seven,zero"]]])

        then:
        getConcerns(apply, "admin") == ["one", "two", "three", "four", "five", "six", "seven", "zero"]
    }

    def "tables are getting extracted from nested arrays"(table) {
        when:
        def apply = createApply(to: ["one", ["two, three", "four", ["/fi.+", "six"]]], concerns: "versioned")

        then:
        getConcerns(apply, table) == ["versioned"]

        where:
        table << ["one", "two", "three", "four", "five", "fiver", "six"]
    }

    List getConcerns(apply, table) {
        apply.concernsForTable(table).collect(Collectors.toList())
    }

    def createApply(content) {
        new Apply(Node.root(content))
    }
}
