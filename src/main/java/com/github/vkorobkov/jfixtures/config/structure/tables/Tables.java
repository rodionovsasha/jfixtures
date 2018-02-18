package com.github.vkorobkov.jfixtures.config.structure.tables;

import com.github.vkorobkov.jfixtures.config.structure.Section;
import com.github.vkorobkov.jfixtures.config.structure.util.TableMatcher;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.CollectionUtil;
import com.github.vkorobkov.jfixtures.util.MapMerger;

import java.util.*;
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
        return readArrayRecursively("before_inserts");
    }

    public List<String> getBeforeCleanup() {
        return readArrayRecursively("before_cleanup");
    }

    public List<String> getAfterInserts() {
        return readArrayRecursively("after_inserts");
    }

    public Map<String, Object> getDefaultColumns() {
        return this.<Map<String, Object>>readProperty(
                MapMerger::merge, "default_columns").orElse(Collections.emptyMap());
    }

    private List<String> readArrayRecursively(String... sections) {
        List<String> instructions = new ArrayList<>();
        CollectionUtil.flattenRecursively(
            readProperty(sections).orElse(Collections.emptyList()),
            element -> instructions.add(String.valueOf(element))
        );
        return instructions;
    }

    private <T> Optional<T> readProperty(String ... sections) {
        return readProperty((current, last) -> last, sections);
    }

    private <T> Optional<T> readProperty(BinaryOperator<T> reducer, String... sections) {
        return this.<T>getMatchingTables()
                .map(node -> node.dig(sections).<T>optional())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(reducer);
    }

    private Stream<Node> getMatchingTables() {
        return getNode().children().filter(this::matchNodeToTable);
    }

    private boolean matchNodeToTable(Node node) {
        Object appliesTo = node.child("applies_to").required();
        return ((TableMatcher)() -> appliesTo).tableMatches(this.name);
    }
}
