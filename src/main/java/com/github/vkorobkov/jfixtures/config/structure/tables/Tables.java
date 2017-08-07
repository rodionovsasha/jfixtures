package com.github.vkorobkov.jfixtures.config.structure.tables;

import com.github.vkorobkov.jfixtures.config.structure.Section;
import com.github.vkorobkov.jfixtures.config.structure.util.TableMatcher;
import com.github.vkorobkov.jfixtures.config.yaml.Node;

import java.util.stream.Stream;

public class Tables extends Section {
    private static final String SECTION_APPLIES_TO = "applies_to";
    private static final String SECTION_PRIMARY_KEY = "pk";
    private static final String SECTION_GENERATE = "generate";
    private static final String SECTION_COLUMN = "column";
    private static final String PK_DEFAULT_COLUMN_NAME = "id";

    private final String name;

    public Tables(Node node, String name) {
        super(node);
        this.name = name;
    }

    public boolean shouldAutoGeneratePk() {
        return (boolean)getMatchingTables()
                .map(node -> node.dig(SECTION_PRIMARY_KEY, SECTION_GENERATE).required())
                .reduce((current, last) -> last).orElse(true);

    }

    public String getCustomColumnForPk() {
        return (String)getMatchingTables()
                .map(node -> node.dig(SECTION_PRIMARY_KEY, SECTION_COLUMN).optional().orElse(PK_DEFAULT_COLUMN_NAME))
                .reduce((current, last) -> last).orElse(PK_DEFAULT_COLUMN_NAME);
    }

    private Stream<Node> getMatchingTables() {
        return getNode()
                .children()
                .filter(
                        node -> {
                            Object appliesTo = node.child(SECTION_APPLIES_TO).required();
                            return ((TableMatcher)() -> appliesTo).tableMatches(this.name);
                        }
                );
    }
}
