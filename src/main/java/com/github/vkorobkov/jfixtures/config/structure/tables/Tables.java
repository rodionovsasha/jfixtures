package com.github.vkorobkov.jfixtures.config.structure.tables;

import com.github.vkorobkov.jfixtures.config.structure.Section;
import com.github.vkorobkov.jfixtures.config.structure.util.SplitStringConsumer;
import com.github.vkorobkov.jfixtures.config.structure.util.TableMatcher;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.CollectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Tables extends Section {
    private static final String SECTION_PRIMARY_KEY = "pk";
    private static final String PK_DEFAULT_COLUMN_NAME = "id";
    private static final String TABLE_NAME_PLACEHOLDER = "$TABLE_NAME";

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
        List<String> instructions = new ArrayList<>();
        CollectionUtil.flattenRecursively(readProperty("before_inserts").orElse(Collections.emptyList()),
                new SplitStringConsumer(instructions::add));
        return instructions;
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> readProperty(String... sections) {
        return (Optional<T>)getMatchingTables()
                .map(node -> node.dig(sections).optional())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce((current, last) -> last);
    }

    private Stream<Node> getMatchingTables() {
        return getNode().children().filter(this::matchNodeToTable);
    }

    private boolean matchNodeToTable(Node node) {
        Object appliesTo = node.child("applies_to").required();
        return ((TableMatcher)() -> appliesTo).tableMatches(this.name);
    }
}
