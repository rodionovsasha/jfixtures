package com.github.vkorobkov.jfixtures.config.structure.tables;

import com.github.vkorobkov.jfixtures.config.structure.Section;
import com.github.vkorobkov.jfixtures.config.structure.util.TableMatcher;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.CollectionUtil;
import com.github.vkorobkov.jfixtures.util.MapMerger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class Tables extends Section {
    private static final String SECTION_PRIMARY_KEY = "pk";
    private static final String PK_DEFAULT_COLUMN_NAME = "id";

    private final String name;

    public Tables(Node node, String name) {
        super(node);
        this.name = name;
    }

    public boolean shouldAutoGeneratePk() {
        return (boolean)readProperty(SECTION_PRIMARY_KEY, "generate").orElse(true);
    }

    public String getPkColumnName() {
        return (String)readProperty(SECTION_PRIMARY_KEY, "column").orElse(PK_DEFAULT_COLUMN_NAME);
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
        return readProperty(MapMerger::merge, "default_columns").orElse(Collections.emptyMap());
    }

    private List readArray(String... sections) {
        List result = new ArrayList();
        readSections(sections).forEach(elem -> CollectionUtil.flattenRecursively(elem, result::add));
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
