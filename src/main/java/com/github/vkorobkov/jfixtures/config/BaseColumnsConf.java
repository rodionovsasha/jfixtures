package com.github.vkorobkov.jfixtures.config;

import com.github.vkorobkov.jfixtures.util.MapMerger;
import com.github.vkorobkov.jfixtures.util.RowMergeConflictResolver;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@AllArgsConstructor
public class BaseColumnsConf {
    private static final String SECTION_BASE = "base_columns";
    private static final String SECTION_APPLY = "apply";
    private static final String SECTION_TO = "to";
    private static final String SECTION_CONCERNS = "concerns";
    private final YamlConfig config;

    @SuppressWarnings("unchecked")
    public Map<String, Object> baseColumns(String table) {
        String sectionBaseApply = SECTION_BASE + ":" + SECTION_APPLY;
        return config
            .digNode(sectionBaseApply).orElse(Collections.emptyMap())
            .keySet().stream()
            .map(section -> sectionBaseApply + ":" + section)
            .filter(section -> tableMatches(section, table))
            .flatMap(this::extractConcerns)
            .map(this::extractColumns)
            .reduce((from, to) ->  (Map<String, Object>)MapMerger.merge(from, to, RowMergeConflictResolver.INSTANCE))
            .orElse(Collections.emptyMap());
    }

    private boolean tableMatches(String section, String table) {
        Set<String> patterns = new HashSet<>();
        visitValuesRecursively(config.digRequiredValue(section, SECTION_TO), patterns::add);

        return patterns.stream().anyMatch(pattern -> {
            if (pattern.startsWith("/")) {
                if (Pattern.compile(pattern.substring(1)).matcher(table).matches()) {
                    return true;
                }
            } else if (pattern.equals(table)) {
                return true;
            }
            return false;
        });
    }

    private Stream<String> extractConcerns(String section) {
        List<String> concerns = new ArrayList<>();
        visitValuesRecursively(config.digRequiredValue(section, SECTION_CONCERNS), concerns::add);
        return concerns.stream();
    }

    private Map<String, Object> extractColumns(String concern) {
        return config.digRequiredNode(SECTION_BASE, SECTION_CONCERNS, concern);
    }

    private void visitValuesRecursively(Object section, Consumer<String> consumer) {
        config.visitValuesRecursively(section, item -> Arrays
            .asList((String.valueOf(item)).split(","))
            .forEach(s -> consumer.accept(s.trim()))
        );
    }
}
