package io.github.rodionovsasha.jfixtures.config.structure

import io.github.rodionovsasha.jfixtures.config.yaml.Node
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RootTest extends Specification {
    def "::empty creates a new config instance"() {
        expect:
        Root.empty()
    }

    def "::ofProfile rejects an unknown non-default profile"() {
        given:
        def config = [
                refs: [users: [role_id: "roles"]],
                profiles: [unit: [
                        refs: [users: [role_id: "unit_roles"]],
                ]]
        ]

        when:
        root(config, "integration")

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == "Configuration profile [integration] is not found"
    }

    def "::ofProfile uses the root when the default profile is not declared"() {
        expect:
        root([refs: [users: [role_id: "roles"]]]).referredTable("users", "role_id").get() == "roles"
    }

    def "::ofProfile creates config with specified profile"() {
        given:
        def config = [
                refs: [users: [role_id: "roles"]],
                profiles: [unit: [
                        refs: [users: [role_id: "unit_roles"]],
                ]]
        ]

        when:
        def root = root(config, "unit")

        then:
        root.referredTable("users", "role_id").get() == "unit_roles"
    }

    def "::referredTable() positive case"() {
        when:
        def root = root(refs: [users: [role_id: "roles"]])

        then:
        root.referredTable("users", "role_id").get() == "roles"
    }

    def "::referredTable() returns empty optional when does not match"(table, column) {
        when:
        def root = root(refs: [users: [role_id: "roles"]])

        then:
        !root.referredTable(table, column).present

        where:
        table   | column
        "sr"    | "roles"
        "users" | "rls"
        "sr"    | "rls"
    }

    def "::referredTable() returns empty optional if refs section does not exist"() {
        expect:
        !root([:]).referredTable("users", "role_id").present
    }

    def "::getCleanTables preserves configured order and duplicates"() {
        expect:
        root([clean_tables: ["logs", "users", "logs"]]).cleanTables == ["logs", "users", "logs"]
    }

    def "::getCleanTables returns an empty list when not configured"() {
        expect:
        root([:]).cleanTables.empty
    }

    def "reads global SQL hooks from a scalar or a list"() {
        given:
        def configuration = root([before_all: "SET before", after_all: ["SET after", ["VACUUM"]]])

        expect:
        configuration.beforeAll == ["SET before"]
        configuration.afterAll == ["SET after", "VACUUM"]
        root([:]).beforeAll.empty
        root([:]).afterAll.empty
    }

    def "reads opt-in templates and configured composite references"() {
        given:
        def configuration = root([
                templates: [enabled: true],
                refs: [line_items: [order: [
                        table: "orders", columns: [tenant_id: "order_tenant_id", order_id: "order_number"]
                ]]]
        ])

        expect:
        configuration.templatesEnabled()
        !root([:]).templatesEnabled()
        configuration.compositeForeignKey("line_items", "order").get().columns() == [
                tenant_id: "order_tenant_id", order_id: "order_number"
        ]
        !configuration.compositeForeignKey("line_items", "missing").present
        !root([refs: [line_items: [order: "orders"]]]).compositeForeignKey("line_items", "order").present
    }

    def "rejects invalid composite-reference column mappings"() {
        when:
        root([refs: [line_items: [order: [table: "orders", columns: [:]]]]])
                .compositeForeignKey("line_items", "order")

        then:
        thrown(IllegalArgumentException)

        when:
        root([refs: [line_items: [order: [table: "orders"]]]]).compositeForeignKey("line_items", "order")

        then:
        !root([refs: [line_items: [order: [table: "orders"]]]]).compositeForeignKey("line_items", "order").present

        when:
        root([refs: [line_items: [order: [table: "orders", columns: "order_id"]]]])
                .compositeForeignKey("line_items", "order")

        then:
        thrown(IllegalArgumentException)

        when:
        root([refs: [line_items: [order: [table: "orders", columns: [(1): "order_id"]]]]])
                .compositeForeignKey("line_items", "order")

        then:
        thrown(IllegalArgumentException)

        when:
        root([refs: [line_items: [order: [table: "orders", columns: [tenant_id: "", order_id: ""]]]]])
                .compositeForeignKey("line_items", "order")

        then:
        thrown(IllegalArgumentException)

        when:
        root([refs: [line_items: [order: [table: "orders", columns: [tenant_id: 1]]]]])
                .compositeForeignKey("line_items", "order")

        then:
        thrown(IllegalArgumentException)

        when:
        root([refs: [line_items: [order: [table: "orders", columns: ["": "order_id"]]]]])
                .compositeForeignKey("line_items", "order")

        then:
        thrown(IllegalArgumentException)

        when:
        root([refs: [line_items: [order: [
                table: "orders", columns: [tenant_id: "order_id", order_id: "order_id"]
        ]]]]).compositeForeignKey("line_items", "order")

        then:
        thrown(IllegalArgumentException)
    }

    def root(content, profile = "default") {
        Root.ofProfile(Node.root(content), profile)
    }
}
