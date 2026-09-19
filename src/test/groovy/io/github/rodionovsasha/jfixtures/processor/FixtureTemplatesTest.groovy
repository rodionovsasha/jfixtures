package io.github.rodionovsasha.jfixtures.processor

import io.github.rodionovsasha.jfixtures.config.structure.Root
import io.github.rodionovsasha.jfixtures.config.yaml.Node
import io.github.rodionovsasha.jfixtures.domain.Row
import io.github.rodionovsasha.jfixtures.domain.Table
import io.github.rodionovsasha.jfixtures.domain.Value
import spock.lang.Specification

class FixtureTemplatesTest extends Specification {
    def "does not expand templates unless explicitly enabled"() {
        given:
        def table = Table.ofRow("users", "user_{{ number }}", ["\$template": "number=1..2"])

        expect:
        FixtureTemplates.expand([table], Root.empty()).first().rows.first().name == "user_{{ number }}"
    }

    def "expands ascending and descending ranges, labels, scalar values, and SQL text"() {
        given:
        def template = Row.of("user_{{ number }}_{{ letter }}", [
                "\$template": "number=1..2, letter=2..1",
                rank         : "{{ (number + 2) * letter - 4 / 2 % 2 }}",
                negative     : "{{ -number }}",
                fixed        : "{{ 100 }}",
                text         : "user-{{ number }}",
                sql          : Value.ofSql("value-{{ letter }}"),
                sqlNumber    : Value.ofSql("{{ number }}"),
                unchanged    : "plain text",
                number       : 1
        ])

        when:
        def rows = FixtureTemplates.expand([Table.of("users", Row.ofName("ordinary"), template)], enabled()).first().rows as List

        then:
        rows*.name == ["ordinary", "user_1_2", "user_1_1", "user_2_2", "user_2_1"]
        rows.tail()*.columns.rank == [Value.of(6L), Value.of(3L), Value.of(8L), Value.of(4L)]
        rows.tail()*.columns.negative == [Value.of(-1L), Value.of(-1L), Value.of(-2L), Value.of(-2L)]
        rows.tail()*.columns.fixed == [Value.of(100L)] * 4
        rows.tail()*.columns.text == [Value.of("user-1"), Value.of("user-1"), Value.of("user-2"), Value.of("user-2")]
        rows.tail()*.columns.sql == [Value.ofSql("value-2"), Value.ofSql("value-1")] * 2
        rows.tail()*.columns.sqlNumber == [Value.ofSql("1"), Value.ofSql("1"), Value.ofSql("2"), Value.ofSql("2")]
        rows.tail()*.columns.unchanged == [Value.of("plain text")] * 4
        rows.tail()*.columns.number == [Value.of(1)] * 4
    }

    def "rejects invalid template directives"() {
        expect:
        fails(Row.of("user", ["\$template": directive]))

        where:
        directive << [1, "number=1", "number=1..1, number=2..2"]
    }

    def "rejects duplicate template rows and oversized expansions"() {
        when:
        FixtureTemplates.expand([Table.of("users",
                Row.of("duplicate", ["\$template": "number=1..1"]),
                Row.of("duplicate", ["\$template": "number=1..1"]))], enabled())

        then:
        thrown(ProcessorException)

        when:
        FixtureTemplates.expand([Table.ofRow("users", "many", ["\$template": "number=1..10001"])], enabled())

        then:
        thrown(ProcessorException)
    }

    def "rejects expressions outside the integer arithmetic grammar"() {
        expect:
        fails(Row.of("user", ["\$template": "number=1..1", value: expression]))

        where:
        expression << ["{{ missing }}", "{{ _number }}", "{{ number_ + 1 }}", "{{ number.thing }}", "{{ (number }}", "{{ - }}",
                       "{{ @ }}", "{{ number / 0 }}", "{{ 9223372036854775807 + 1 }}",
                       "{{ 999999999999999999999999999999 }}"]
    }

    private static Root enabled() {
        Root.ofProfile(Node.root([templates: [enabled: true]]), "default")
    }

    private static boolean fails(Row row) {
        try {
            FixtureTemplates.expand([Table.of("users", row)], enabled())
            false
        } catch (ProcessorException ignored) {
            true
        }
    }
}
