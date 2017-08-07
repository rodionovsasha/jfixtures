package com.github.vkorobkov.jfixtures.config.structure.columns;

import com.github.vkorobkov.jfixtures.config.structure.Section;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.MapMerger;
import com.github.vkorobkov.jfixtures.util.RowMergeConflictResolver;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public class Columns extends Section {
    public Columns(Node node) {
        super(node);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> forTable(String table) {
        return applyStream()
            .flatMap(apply -> apply.concernsForTable(table))
            .map(this::getConcern)
            .reduce((from, to) -> (Map<String, Object>) MapMerger.merge(from, to, RowMergeConflictResolver.INSTANCE))
            .orElse(Collections.emptyMap());
    }

    private Stream<Apply> applyStream() {
        return getNode().child("apply").children().map(Apply::new);
    }

    private Map<String, Object> getConcern(String concern) {
        return getNode().dig("concerns", concern).required();
    }
}
