package com.github.vkorobkov.jfixtures.config.structure;

import com.github.vkorobkov.jfixtures.config.structure.columns.Columns;
import com.github.vkorobkov.jfixtures.config.structure.tables.Tables;
import com.github.vkorobkov.jfixtures.config.yaml.Node;

import java.util.Optional;

public class Root extends Section {
    public Root(Node node) {
        super(node);
    }

    public Optional<String> referredTable(String table, String column) {
        return getNode().dig("refs", table, column).optional();
    }

    public Columns columns() {
        return new Columns(getNode().child("columns"));
    }

    public Tables table(String name) {
        return new Tables(getNode().child("tables"), name);
    }
}
