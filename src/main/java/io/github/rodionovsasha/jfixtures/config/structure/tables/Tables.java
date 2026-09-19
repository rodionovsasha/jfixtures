package io.github.rodionovsasha.jfixtures.config.structure.tables;

import io.github.rodionovsasha.jfixtures.config.structure.Section;
import io.github.rodionovsasha.jfixtures.config.structure.util.TableMatcher;
import io.github.rodionovsasha.jfixtures.config.yaml.Node;
import io.github.rodionovsasha.jfixtures.util.CollectionUtil;
import io.github.rodionovsasha.jfixtures.util.MapMerger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class Tables extends Section {
    private static final String SECTION_PRIMARY_KEY = "pk";
    private static final String PK_DEFAULT_COLUMN_NAME = "id";
    private static final String PK_DEFAULT_TYPE = "int";

    private final String name;

    public Tables(Node node, String name) {
        super(node);
        this.name = name;
    }

    public boolean shouldAutoGeneratePk() {
        return (boolean)readProperty(SECTION_PRIMARY_KEY, "generate").orElse(true);
    }

    public String getPkColumnName() {
        return getPkColumnNames().get(0);
    }

    public Optional<String> getIdGenerator() {
        return readProperty(SECTION_PRIMARY_KEY, "id_generator")
                .map(String.class::cast)
                .or(() -> readProperty("id_generator").map(String.class::cast));
    }

    public List<String> getRequires() {
        return readArray("requires");
    }

    public Optional<Object> getTimestampValue() {
        return readProperty("timestamps", "value");
    }

    public boolean shouldAddTimestamps() {
        return readProperty("timestamps", "enabled").map(Boolean.class::cast).orElse(false)
                || readProperty("timestamps").filter(Boolean.class::isInstance).map(Boolean.class::cast).orElse(false);
    }

    /**
     * Returns the primary-key columns in declaration order.  {@code pk.column}
     * remains supported for existing configurations; new composite keys use
     * {@code pk.columns}.
     */
    public List<String> getPkColumnNames() {
        Object columns = readProperty(SECTION_PRIMARY_KEY, "columns").orElse(null);
        if (columns == null) {
            return Collections.singletonList(
                    computedColumnName((String)readProperty(SECTION_PRIMARY_KEY, "column")
                            .orElse(PK_DEFAULT_COLUMN_NAME))
            );
        }
        if (!(columns instanceof Collection<?> values) || values.isEmpty()) {
            throw new IllegalArgumentException("Primary-key columns must be a non-empty list");
        }
        List<String> result = new ArrayList<>();
        for (Object value : values) {
            if (!(value instanceof String name) || name.isBlank()) {
                throw new IllegalArgumentException("Primary-key columns must contain non-blank strings");
            }
            result.add(computedColumnName(name));
        }
        if (new LinkedHashSet<>(result).size() != result.size()) {
            throw new IllegalArgumentException("Primary-key columns must not contain duplicates");
        }
        return Collections.unmodifiableList(result);
    }

    private String computedColumnName(String configuredName) {
        String tableName = name.replace('.', '_');
        return configuredName
                .replace("${TABLE}", tableName.toUpperCase(java.util.Locale.ROOT))
                .replace("${table}", tableName.toLowerCase(java.util.Locale.ROOT));
    }

    public boolean shouldGenerateUuidPk() {
        String type = (String)readProperty(SECTION_PRIMARY_KEY, "type").orElse(PK_DEFAULT_TYPE);
        if (!"int".equalsIgnoreCase(type) && !"uuid".equalsIgnoreCase(type)) {
            throw new IllegalArgumentException("Unsupported primary-key type [" + type + "]");
        }
        return "uuid".equalsIgnoreCase(type);
    }

    public CleanMethod getCleanMethod() {
        return CleanMethod.valueOfIgnoreCase((String)readProperty("clean_method").orElse("delete"));
    }

    public List<String> getBeforeInserts() {
        return readArray("before_inserts");
    }

    public List<String> getBeforeCleanup() {
        return readArray("before_cleanup");
    }

    public List<String> getAfterInserts() {
        return readArray("after_inserts");
    }

    public Map<String, Object> getDefaultColumns() {
        return this.<Map<String, Object>>readProperty(MapMerger::merge, "default_columns")
                .orElse(Collections.emptyMap());
    }

    private List<String> readArray(String... sections) {
        List<String> result = new ArrayList<>();
        readSections(sections).forEach(elem ->
                CollectionUtil.flattenRecursively(elem, value -> result.add(String.class.cast(value))));
        return result;
    }

    private <T> Optional<T> readProperty(String... sections) {
        return readProperty((current, last) -> last, sections);
    }

    private <T> Optional<T> readProperty(BinaryOperator<T> reducer, String... sections) {
        return this.<T>readSections(sections).reduce(reducer);
    }

    private <T> Stream<T> readSections(String... sections) {
        return this.getMatchingTables()
            .map(node -> node.dig(sections).<T>optional())
            .filter(Optional::isPresent)
            .map(Optional::get);
    }

    private Stream<Node> getMatchingTables() {
        return getNode().children().filter(this::matchNodeToTable);
    }

    private boolean matchNodeToTable(Node node) {
        Object appliesTo = node.child("applies_to").required();
        return ((TableMatcher)() -> appliesTo).tableMatches(this.name);
    }
}
