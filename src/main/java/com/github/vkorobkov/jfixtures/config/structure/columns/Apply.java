package com.github.vkorobkov.jfixtures.config.structure.columns;

import com.github.vkorobkov.jfixtures.config.structure.Section;
import com.github.vkorobkov.jfixtures.config.structure.util.SplitStringConsumer;
import com.github.vkorobkov.jfixtures.config.structure.util.TableMatcher;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


class Apply extends Section implements TableMatcher {
    Apply(Node node) {
        super(node);
    }

    Stream<String> concernsForTable(String table) {
        return tableMatches(table) ? getConcerns() : Stream.empty();
    }

    @Override
    public Object tablesToMatch() {
        return getNode().child("to").required();
    }

    private Stream<String> getConcerns() {
        List<String> result = new ArrayList<>();
        CollectionUtil.flattenRecursively(getNode().child("concerns").required(),
            new SplitStringConsumer(result::add));
        return result.stream();
    }
}
