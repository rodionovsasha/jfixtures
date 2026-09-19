package io.github.rodionovsasha.jfixtures.config.structure;

import io.github.rodionovsasha.jfixtures.config.structure.tables.Tables;
import io.github.rodionovsasha.jfixtures.config.yaml.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

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

    public Optional<CompositeForeignKey> compositeForeignKey(String table, String column) {
        return getNode().dig("refs", table, column).optional().flatMap(this::compositeForeignKey);
    }

    public boolean templatesEnabled() {
        return getNode().dig("templates", "enabled").<Boolean>optional().orElse(false);
    }

    public Optional<String> getIdGenerator() {
        return getNode().child("id_generator").optional().map(String.class::cast);
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

    @SuppressWarnings("unchecked")
    private Optional<CompositeForeignKey> compositeForeignKey(Object value) {
        if (!(value instanceof Map<?, ?> raw) || !raw.containsKey("columns")) {
            return Optional.empty();
        }
        Map<String, Object> values = (Map<String, Object>)raw;
        Object rawColumns = values.get("columns");
        if (!(rawColumns instanceof Map<?, ?> columns) || columns.isEmpty()) {
            throw new IllegalArgumentException("Composite reference columns must be a non-empty map");
        }
        java.util.LinkedHashMap<String, String> mapping = new java.util.LinkedHashMap<>();
        columns.forEach((target, source) -> {
            if (!(target instanceof String targetName) || !(source instanceof String sourceName)
                    || targetName.isBlank() || sourceName.isBlank()) {
                throw new IllegalArgumentException("Composite reference columns must map non-blank strings");
            }
            mapping.put(targetName, sourceName);
        });
        Set<String> sourceColumns = new HashSet<>(mapping.values());
        if (sourceColumns.size() != mapping.size()) {
            throw new IllegalArgumentException("Composite reference columns must write distinct source columns");
        }
        return Optional.of(new CompositeForeignKey(required(values, "table"), mapping));
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

    public record CompositeForeignKey(String table, Map<String, String> columns) {
        public CompositeForeignKey {
            columns = Collections.unmodifiableMap(new java.util.LinkedHashMap<>(columns));
        }
    }

    public record PolymorphicReference(String idColumn, String typeColumn, Map<String, String> types) {
        public PolymorphicReference {
            types = Map.copyOf(types);
        }
    }

    public record ManyToMany(String joinTable, String sourceColumn, String targetTable, String targetColumn) { }
}
