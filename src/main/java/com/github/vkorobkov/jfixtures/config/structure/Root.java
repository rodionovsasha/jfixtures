package com.github.vkorobkov.jfixtures.config.structure;

import com.github.vkorobkov.jfixtures.config.structure.tables.Tables;
import com.github.vkorobkov.jfixtures.config.yaml.Node;

import java.util.Optional;

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
}
