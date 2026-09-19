package io.github.rodionovsasha.jfixtures.config.structure;

import io.github.rodionovsasha.jfixtures.config.structure.tables.Tables;
import io.github.rodionovsasha.jfixtures.config.yaml.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public final class Root extends Section {
    public static Root ofProfile(Node root, String profile) {
        Node profileNode = root.dig("profiles", profile);
        Node node = profileNode.exists() ? profileNode : root;
        return new Root(node);
    }

    public static Root empty() {
        return new Root(Node.emptyRoot());
    }

    private Root(Node node) {
        super(node);
    }

    public Optional<ForeignKey> foreignKey(String table, String column) {
        return getNode().dig("refs", table, column).optional().map(this::foreignKey);
    }

    /** Kept for source compatibility with the original string-only reference configuration. */
    public Optional<String> referredTable(String table, String column) {
        return foreignKey(table, column).map(ForeignKey::table);
    }

    public Optional<PolymorphicReference> polymorphicReference(String table, String column) {
        return section("polymorphic_refs", table, column).map(this::polymorphicReference);
    }

    public Optional<ManyToMany> manyToMany(String table, String column) {
        return section("many_to_many", table, column).map(this::manyToMany);
    }

    public Tables table(String name) {
        return new Tables(getNode().child("tables"), name);
    }

    public List<String> getCleanTables() {
        return getNode().child("clean_tables").<Collection<?>>optional()
                .orElse(Collections.emptyList())
                .stream()
                .map(String.class::cast)
                .collect(toList());
    }

    private Optional<Map<String, Object>> section(String section, String table, String column) {
        return getNode().dig(section, table, column).<Map<String, Object>>optional();
    }

    @SuppressWarnings("unchecked")
    private ForeignKey foreignKey(Object value) {
        if (value instanceof String table) {
            return new ForeignKey(table, null);
        }
        Map<String, Object> values = (Map<String, Object>)value;
        return new ForeignKey(required(values, "table"), optional(values, "column"));
    }

    private PolymorphicReference polymorphicReference(Map<String, Object> values) {
        Map<String, String> types = values.containsKey("types")
                ? ((Map<String, Object>)values.get("types")).entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> String.valueOf(entry.getValue())
                    ))
                : Collections.emptyMap();
        return new PolymorphicReference(
                required(values, "id_column"),
                required(values, "type_column"),
                types
        );
    }

    private ManyToMany manyToMany(Map<String, Object> values) {
        return new ManyToMany(
                required(values, "join_table"),
                required(values, "source_column"),
                required(values, "target_table"),
                required(values, "target_column")
        );
    }

    private String required(Map<String, Object> values, String key) {
        String value = optional(values, key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing configuration property [" + key + "]");
        }
        return value;
    }

    private String optional(Map<String, Object> values, String key) {
        Object value = values.get(key);
        return value == null ? null : String.valueOf(value);
    }

    public record ForeignKey(String table, String column) { }

    public record PolymorphicReference(String idColumn, String typeColumn, Map<String, String> types) {
        public PolymorphicReference {
            types = Map.copyOf(types);
        }
    }

    public record ManyToMany(String joinTable, String sourceColumn, String targetTable, String targetColumn) { }
}
