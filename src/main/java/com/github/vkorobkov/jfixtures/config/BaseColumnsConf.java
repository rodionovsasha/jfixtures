package com.github.vkorobkov.jfixtures.config;

import com.github.vkorobkov.jfixtures.util.MapMerger;
import com.github.vkorobkov.jfixtures.util.RowMergeConflictResolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class BaseColumnsConf extends Config {
    private static final String SECTION_BASE = "base_columns";
    private static final String SECTION_APPLY = "apply";
    private static final String SECTION_TO = "to";
    private static final String SECTION_CONCERNS = "concerns";

    public BaseColumnsConf(final YamlConfig yamlConf) {
        super(yamlConf);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getBaseColumns(String tableName) {
        String sectionBaseApply = SECTION_BASE + ":" + SECTION_APPLY;
        return getYamlConfig()
            .digNode(sectionBaseApply).orElse(Collections.emptyMap())
            .keySet().stream()
            .map(section -> sectionBaseApply + ":" + section)
            .filter(section -> tableMatches(section, tableName, SECTION_TO))
            .flatMap(this::extractConcerns)
            .map(this::extractColumns)
            .reduce((from, to) ->  (Map<String, Object>)MapMerger.merge(from, to, RowMergeConflictResolver.INSTANCE))
            .orElse(Collections.emptyMap());
    }

    private Stream<String> extractConcerns(String section) {
        List<String> concerns = new ArrayList<>();
        visitValuesRecursively(getYamlConfig().digRequiredValue(section, SECTION_CONCERNS), concerns::add);
        return concerns.stream();
    }

    private Map<String, Object> extractColumns(String concern) {
        return getYamlConfig().digRequiredNode(SECTION_BASE, SECTION_CONCERNS, concern);
    }
}
