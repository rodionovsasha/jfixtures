package io.github.rodionovsasha.jfixtures.config.structure;

import io.github.rodionovsasha.jfixtures.config.structure.tables.Tables;
import io.github.rodionovsasha.jfixtures.config.yaml.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    public Optional<String> referredTable(String table, String column) {
        return getNode().dig("refs", table, column).optional();
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
}
