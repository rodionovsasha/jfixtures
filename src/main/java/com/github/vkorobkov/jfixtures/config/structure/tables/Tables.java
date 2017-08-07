package com.github.vkorobkov.jfixtures.config.structure.tables;

import com.github.vkorobkov.jfixtures.config.structure.Section;
import com.github.vkorobkov.jfixtures.config.structure.util.TableMatcher;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

public class Table extends Section {
    private static final String SECTION_TABLES = "tables";
    private static final String SECTION_APPLIES_TO = "applies_to";
    private static final String SECTION_PRIMARY_KEY = "pk";
    private static final String SECTION_GENERATE = "generate";
    private static final String SECTION_COLUMN = "column";
    private static final String PK_DEFAULT_COLUMN_NAME = "id";

    @Getter
    private final String name;

    public Table(Node table, String name) {
        super(table);
        this.name = name;
    }

    public boolean shouldAutoGeneratePk() {
        /*return getMatchingTables()
                .map(this::extractGenerateValue)
                .reduce((current, last) -> last).orElse(true);*/
        return (boolean)getNode()
                .children()
                .filter(
                        node -> {
                            Object appliesTo = getNode().child(SECTION_APPLIES_TO);
                            return ((TableMatcher)() -> appliesTo).tableMatches(this.name);
                        }
                )
                .map(node -> node.dig(SECTION_PRIMARY_KEY, SECTION_GENERATE).required())
                .reduce((current, last) -> last).orElse(true);


    }

    public String getCustomColumnForPk() {
        /*return getMatchingTables()
                .map(this::getColumnValue)
                .reduce((current, last) -> last).orElse(PK_DEFAULT_COLUMN_NAME);*/

        return (String)getNode()
                .children()
                .filter(
                        node -> {
                            Object appliesTo = getNode().child(SECTION_APPLIES_TO).required();
                            System.out.println("*****" + appliesTo);
                            System.out.println("*****" + this.name);
                            return ((TableMatcher)() -> appliesTo).tableMatches(this.name);
                        }

                )
                .map(node -> node.dig(SECTION_PRIMARY_KEY, SECTION_COLUMN).optional().orElse(PK_DEFAULT_COLUMN_NAME))
                .reduce((current, last) -> last).orElse(PK_DEFAULT_COLUMN_NAME);
    }

    public Stream<Node> getMatchingTables() {
        /*return getNode().child(SECTION_TABLES).children()
                .map(section -> SECTION_TABLES + ":" + section)
                .filter(section -> tableMatches(section, tableName, SECTION_APPLIES_TO))
                ;*/
         return getNode()
                .children()
                .filter(
                        node -> {
                            Object appliesTo = getNode().child(SECTION_APPLIES_TO);
                            return ((TableMatcher)() -> appliesTo).tableMatches(this.name);
                        }
                );



                /*getYamlConfig()
                .digNode(SECTION_TABLES).orElse(Collections.emptyMap())
                .keySet().stream()
                .map(section -> SECTION_TABLES + ":" + section)
                .filter(section -> tableMatches(section, tableName, SECTION_APPLIES_TO));*/
    }

    public Optional<Node> getMatchingTables2() {
        /*return getNode().child(SECTION_TABLES).children()
                .map(section -> SECTION_TABLES + ":" + section)
                .filter(section -> tableMatches(section, tableName, SECTION_APPLIES_TO))
                ;*/
        return getNode()
                .children()
                .filter(
                        node -> {
                            Object appliesTo = getNode().child(SECTION_APPLIES_TO).required();
                            return ((TableMatcher)() -> appliesTo).tableMatches(this.name);
                        }
                ).findAny();



                /*getYamlConfig()
                .digNode(SECTION_TABLES).orElse(Collections.emptyMap())
                .keySet().stream()
                .map(section -> SECTION_TABLES + ":" + section)
                .filter(section -> tableMatches(section, tableName, SECTION_APPLIES_TO));*/
    }

    /*private Stream<Node> tablesStream() {
        return getNode().children();
    }*/

    /*private String getColumnValue(String section) {
        return (String)getNode()
                .dig(section, SECTION_PRIMARY_KEY, SECTION_COLUMN).optional()
                .orElse(PK_DEFAULT_COLUMN_NAME);
    }*/

    /*private boolean extractGenerateValue(String section) {
        return getNode().dig(section, SECTION_PRIMARY_KEY, SECTION_GENERATE).required();
    }*/
}
