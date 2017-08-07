package com.github.vkorobkov.jfixtures.config.structure.tables;

import com.github.vkorobkov.jfixtures.config.structure.Section;
import com.github.vkorobkov.jfixtures.config.structure.util.TableMatcher;
import com.github.vkorobkov.jfixtures.config.yaml.Node;

import java.util.Optional;
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
        return (boolean)readProperty(SECTION_PRIMARY_KEY, SECTION_GENERATE).orElse(true);
    }

    public String getCustomColumnForPk() {
        return (String)readProperty(SECTION_PRIMARY_KEY, SECTION_COLUMN).orElse(PK_DEFAULT_COLUMN_NAME);
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> readProperty(String... sections) {
        Optional result = getMatchingTables()
                .map(node -> node.dig(sections).optional())
                .reduce((current, last) -> last);
        return (Optional<T>)result.orElse(result);
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
