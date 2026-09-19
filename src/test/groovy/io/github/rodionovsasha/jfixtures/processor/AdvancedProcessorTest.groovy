package io.github.rodionovsasha.jfixtures.processor

import io.github.rodionovsasha.jfixtures.IntId
import io.github.rodionovsasha.jfixtures.JFixtures
import io.github.rodionovsasha.jfixtures.UuidId
import io.github.rodionovsasha.jfixtures.config.structure.Root
import io.github.rodionovsasha.jfixtures.config.yaml.Node
import io.github.rodionovsasha.jfixtures.domain.Table
import io.github.rodionovsasha.jfixtures.domain.Value
import io.github.rodionovsasha.jfixtures.instructions.InsertRow
import io.github.rodionovsasha.jfixtures.instructions.CustomSql
import io.github.rodionovsasha.jfixtures.result.Result
import org.h2.jdbcx.JdbcDataSource
import spock.lang.Specification

import java.sql.DriverManager
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

class AdvancedProcessorTest extends Specification {

    def "resolves relative inline foreign keys and a target's non-primary-key column"() {
        given:
        def users = Table.ofRow("admin.users", "vlad", [id: 7, public_id: "public-vlad"])
        def comments = Table.ofRow("admin.comments", "comment", [user_public_id: "users:vlad:public_id"])

        when:
        def rows = rows(new Processor([comments, users], Root.empty()).process())

        then:
        rows.find { it.table == "admin.comments" }.values.user_public_id == Value.of("public-vlad")
    }

    def "interpolates labels and exposes deterministic integer identifiers"() {
        given:
        def fixtures = Table.of("users",
                io.github.rodionovsasha.jfixtures.domain.Row.of("vlad", [slug: 'user-$LABEL']),
                io.github.rodionovsasha.jfixtures.domain.Row.of("kate", [vlad_id: '$ID(vlad)']))

        when:
        def fixtureRows = rows(new Processor([fixtures], Root.empty()).process())

        then:
        fixtureRows.find { it.rowName == "vlad" }.values.slug == Value.of("user-vlad")
        fixtureRows.find { it.rowName == "kate" }.values.vlad_id == Value.of(IntId.one("vlad"))

        and:
        def sqlRows = rows(new Processor([Table.ofRow("sql_users", "vlad", [slug: Value.ofSql('tag-$LABEL')])], Root.empty()).process())
        sqlRows.first().values.slug == Value.ofSql("tag-vlad")
    }

    def "generates stable UUID primary keys and resolves configured label references"() {
        given:
        def config = root(
                refs: [comments: [author_id: "users"]],
                tables: [uuid_users: [applies_to: "users", pk: [type: "uuid"]]]
        )
        def users = Table.ofRow("users", "vlad", [name: "Vlad"])
        def comments = Table.ofRow("comments", "comment", [author_id: "vlad"])

        when:
        def rows = rows(new Processor([comments, users], config).process())

        then:
        def uuid = Value.of(UuidId.one("vlad").toString())
        rows.find { it.table == "users" }.values.id == uuid
        rows.find { it.table == "comments" }.values.author_id == uuid
    }

    def "generates every composite primary-key component and expands configured references"() {
        given:
        def config = root(
                tables: [orders: [applies_to: "orders", pk: [columns: ["tenant_id", "order_id"]]]],
                refs: [line_items: [order: [
                        table: "orders", columns: [tenant_id: "order_tenant_id", order_id: "order_number"]
                ]]]
        )
        def orders = Table.ofRow("orders", "spring_sale", [description: "Spring sale"])
        def lineItems = Table.ofRow("line_items", "first", [order: "spring_sale", sku: "A-1"])

        when:
        def rows = rows(new Processor([lineItems, orders], config).process())

        then:
        def order = rows.find { it.table == "orders" }
        order.values.tenant_id == Value.of(IntId.one("spring_sale.tenant_id"))
        order.values.order_id == Value.of(IntId.one("spring_sale.order_id"))

        and:
        def lineItem = rows.find { it.table == "line_items" }
        lineItem.values.order_tenant_id == order.values.tenant_id
        lineItem.values.order_number == order.values.order_id
        !lineItem.values.containsKey("order")
    }

    def "validates complete explicit composite keys and rejects ambiguous scalar references"() {
        given:
        def keyConfig = root(tables: [orders: [applies_to: "orders", pk: [
                generate: false, columns: ["tenant_id", "order_id"]
        ]]])

        when:
        new Processor([Table.ofRow("orders", "incomplete", [tenant_id: 1])], keyConfig).process()

        then:
        def incomplete = thrown(ProcessorException)
        incomplete.message.contains("missing column [order_id]")

        when:
        new Processor([Table.of("orders",
                io.github.rodionovsasha.jfixtures.domain.Row.of("one", [tenant_id: 1, order_id: 2]),
                io.github.rodionovsasha.jfixtures.domain.Row.of("two", [tenant_id: 1, order_id: 2]))], keyConfig).process()

        then:
        def duplicate = thrown(ProcessorException)
        duplicate.message.contains("tenant_id=1, order_id=2")

        when:
        def config = root(tables: [orders: [applies_to: "orders", pk: [columns: ["tenant_id", "order_id"]]]],
                refs: [line_items: [order_id: "orders"]])
        new Processor([Table.ofRow("orders", "one", [:]), Table.ofRow("line_items", "first", [order_id: "one"])], config).process()

        then:
        thrown(ProcessorException)
    }

    def "rejects incomplete composite-reference mappings, blank labels, and composite many-to-many sources"() {
        given:
        def orders = Table.ofRow("orders", "one", [:])

        when:
        new Processor([Table.ofRow("line_items", "line", [order: "one"]), orders], root(
                tables: [orders: [applies_to: "orders", pk: [columns: ["tenant_id", "order_id"]]]],
                refs: [line_items: [order: [table: "orders", columns: [tenant_id: "order_tenant_id"]]]]
        )).process()

        then:
        thrown(ProcessorException)

        when:
        new Processor([Table.ofRow("line_items", "line", [order: ""]), orders], root(
                tables: [orders: [applies_to: "orders", pk: [columns: ["tenant_id", "order_id"]]]],
                refs: [line_items: [order: [
                        table: "orders", columns: [tenant_id: "order_tenant_id", order_id: "order_number"]
                ]]]
        )).process()

        then:
        thrown(ProcessorException)

        when:
        new Processor([Table.ofRow("line_items", "line", [order: 1]), orders], root(
                tables: [orders: [applies_to: "orders", pk: [columns: ["tenant_id", "order_id"]]]],
                refs: [line_items: [order: [
                        table: "orders", columns: [tenant_id: "order_tenant_id", order_id: "order_number"]
                ]]]
        )).process()

        then:
        thrown(ProcessorException)

        when:
        new Processor([Table.ofRow("posts", "post", [tags: ["blue"]]), Table.ofRow("tags", "blue", [:])], root(
                tables: [posts: [applies_to: "posts", pk: [columns: ["tenant_id", "post_id"]]]],
                many_to_many: [posts: [tags: [
                        join_table: "posts_tags", source_column: "post_id", target_table: "tags", target_column: "tag_id"
                ]]]
        )).process()

        then:
        thrown(ProcessorException)
    }

    def "expands opt-in template rows with integer arithmetic and keeps templates disabled by default"() {
        given:
        def template = io.github.rodionovsasha.jfixtures.domain.Row.of("user_{{ number }}", [
                "\$template": "number=1..3", name: "User {{ number }}", position: "{{ number * 10 }}"
        ])

        when:
        def templateRows = rows(new Processor([Table.of("users", template)], root(templates: [enabled: true])).process())

        then:
        templateRows*.rowName == ["user_1", "user_2", "user_3"]
        templateRows*.values.position == [Value.of(10L), Value.of(20L), Value.of(30L)]
        templateRows*.values.name == [Value.of("User 1"), Value.of("User 2"), Value.of("User 3")]

        when:
        def disabled = rows(new Processor([Table.ofRow("users", "user", [position: "{{ 1 + 2 }}"])], Root.empty()).process())

        then:
        disabled.first().values.position == Value.of("{{ 1 + 2 }}")
    }

    def "rejects template expressions outside the safe arithmetic grammar"() {
        given:
        def row = io.github.rodionovsasha.jfixtures.domain.Row.of("user", [
                "\$template": "number=1..1", value: "{{ number.toString() }}"
        ])

        when:
        new Processor([Table.of("users", row)], root(templates: [enabled: true])).process()

        then:
        thrown(ProcessorException)
    }

    def "expands configured polymorphic references"() {
        given:
        def config = root(polymorphic_refs: [fruits: [eater: [
                id_column: "eater_id", type_column: "eater_type", types: [Monkey: "monkeys"]
        ]]])
        def monkeys = Table.ofRow("monkeys", "george", [name: "George"])
        def fruits = Table.ofRow("fruits", "apple", [eater: "george (Monkey)"])

        when:
        def rows = rows(new Processor([fruits, monkeys], config).process())

        then:
        def apple = rows.find { it.table == "fruits" }
        apple.values.eater_id == rows.find { it.table == "monkeys" }.values.id
        apple.values.eater_type == Value.of("Monkey")
        !apple.values.containsKey("eater")
    }

    def "expands inline many-to-many labels into join-table rows"() {
        given:
        def config = root(many_to_many: [posts: [tags: [
                join_table: "posts_tags", source_column: "post_id", target_table: "tags", target_column: "tag_id"
        ]]])
        def tags = Table.of("tags",
                io.github.rodionovsasha.jfixtures.domain.Row.of("blue", [name: "Blue"]),
                io.github.rodionovsasha.jfixtures.domain.Row.of("green", [name: "Green"]))
        def posts = Table.ofRow("posts", "first", [title: "First", tags: ["blue", "green"]])

        when:
        def rows = rows(new Processor([posts, tags], config).process())

        then:
        def joins = rows.findAll { it.table == "posts_tags" }
        joins*.values == [
                [post_id: Value.of(IntId.one("first")), tag_id: Value.of(IntId.one("blue"))],
                [post_id: Value.of(IntId.one("first")), tag_id: Value.of(IntId.one("green"))]
        ]
    }

    def "applies compiled SQL through a JDBC connection"() {
        given:
        def connection = DriverManager.getConnection("jdbc:h2:mem:jfixtures_jdbc;DB_CLOSE_DELAY=-1")
        connection.createStatement().execute('CREATE TABLE "users" ("id" INT PRIMARY KEY, "name" VARCHAR(50))')

        when:
        JFixtures.noConfig().addTables(Table.ofRow("users", "vlad", [name: "Vlad"])).apply(connection)

        then:
        def result = connection.createStatement().executeQuery('SELECT "name" FROM "users"')
        result.next()
        result.getString(1) == "Vlad"

        cleanup:
        connection.close()
    }

    def "applies fixtures through a data source and supports empty custom SQL"() {
        given:
        def dataSource = new JdbcDataSource(URL: "jdbc:h2:mem:jfixtures_datasource;DB_CLOSE_DELAY=-1")
        dataSource.connection.createStatement().execute('CREATE TABLE "users" ("id" INT PRIMARY KEY, "name" VARCHAR(50))')
        def fixtures = JFixtures.noConfig().addTables(Table.ofRow("users", "vlad", [name: "Vlad"]))

        when:
        fixtures.compile().toSql99().apply(dataSource)
        fixtures.compile().apply(dataSource)
        fixtures.apply(dataSource)
        new Result([new CustomSql("users", "")]).apply(dataSource.connection)

        then:
        def result = dataSource.connection.createStatement().executeQuery('SELECT COUNT(*) FROM "users"')
        result.next()
        result.getInt(1) == 1
    }

    def "validates advanced association configuration and input"() {
        expect:
        root(refs: [comments: [author_id: [table: "users"]]]).foreignKey("comments", "author_id").get().column() == null
        !root(polymorphic_refs: [comments: [author: [id_column: "author_id", type_column: "author_type"]]])
                .polymorphicReference("comments", "author").get().types()

        when:
        root(many_to_many: [posts: [tags: [join_table: ""]]]).manyToMany("posts", "tags")

        then:
        thrown(IllegalArgumentException)

        when:
        root(many_to_many: [posts: [tags: [
                join_table: "posts_tags", source_column: "post_id", target_table: "tags"
        ]]]).manyToMany("posts", "tags")

        then:
        thrown(IllegalArgumentException)
    }

    def "rejects invalid advanced association values"() {
        when:
        new Processor([Table.ofRow("posts", "post", [tags: ["blue"]])], Root.empty()).process()

        then:
        thrown(ProcessorException)

        when:
        new Processor([Table.ofRow("fruits", "apple", [eater: "george"] )], root(polymorphic_refs: [fruits: [eater: [
                id_column: "eater_id", type_column: "eater_type", types: [Monkey: "monkeys"]
        ]]])).process()

        then:
        thrown(ProcessorException)

        when:
        new Processor([Table.ofRow("fruits", "apple", [eater: "george (Person)"])], root(polymorphic_refs: [fruits: [eater: [
                id_column: "eater_id", type_column: "eater_type", types: [Monkey: "monkeys"]
        ]]])).process()

        then:
        thrown(ProcessorException)
    }

    def "rejects malformed many-to-many association values"() {
        given:
        def config = root(many_to_many: [posts: [tags: [
                join_table: "posts_tags", source_column: "post_id", target_table: "tags", target_column: "tag_id"
        ]]])

        expect:
        fails(config, "not-a-list")
        fails(config, [1])
        fails(config, ["blue", "blue"])
    }

    def "uses a root table when a relative inline target does not exist"() {
        given:
        def context = new Context([Table.ofName("profiles")], Root.empty())

        expect:
        context.resolveTableName("admin.comments", "profiles") == "profiles"
        context.resolveTableName("comments", "profiles") == "profiles"
        new Context([Table.ofName("admin.profiles")], Root.empty())
                .resolveTableName("admin.comments", "profiles") == "admin.profiles"
        new Context([Table.ofName("elsewhere")], Root.empty())
                .resolveTableName("admin.comments", "profiles") == "profiles"
    }

    def "rejects unsupported primary-key types and a many-to-many source without an identifier"() {
        when:
        new Processor([Table.ofRow("users", "vlad", [:])], root(tables: [users: [applies_to: "users", pk: [type: "text"]]])).process()

        then:
        thrown(IllegalArgumentException)

        when:
        new Processor([Table.ofRow("posts", "post", [tags: ["blue"]])], root(
                tables: [posts: [applies_to: "posts", pk: [generate: false]]],
                many_to_many: [posts: [tags: [
                        join_table: "posts_tags", source_column: "post_id", target_table: "tags", target_column: "tag_id"
                ]]]
        )).process()

        then:
        thrown(ProcessorException)
    }

    def "reports a data-source connection failure"() {
        given:
        DataSource broken = [getConnection: { throw new SQLException("unavailable") }] as DataSource

        when:
        new Result([]).toSql99().apply(broken)

        then:
        thrown(JdbcException)

        when:
        Connection brokenConnection = [createStatement: { throw new SQLException("unavailable") }] as Connection
        new Result([new CustomSql("users", "SELECT 1")]).apply(brokenConnection)

        then:
        thrown(JdbcException)
    }

    private static Root root(Map values) {
        Root.ofProfile(Node.root(values), "default")
    }

    private static List<InsertRow> rows(Collection instructions) {
        instructions.findAll { it instanceof InsertRow } as List<InsertRow>
    }

    private static boolean fails(Root config, Object value) {
        try {
            def target = Table.ofRow("tags", "blue", [name: "Blue"])
            new Processor([Table.ofRow("posts", "post", [tags: value]), target], config).process()
            false
        } catch (ProcessorException ignored) {
            true
        }
    }
}
