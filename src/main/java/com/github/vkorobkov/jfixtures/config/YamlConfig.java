package com.github.vkorobkov.jfixtures.config;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class YamlConfig {
    private static final String KEY_SEPARATOR = ":";
    private final Map<String, Object> yaml;

    public YamlConfig(Map<String, Object> yaml) {
        this.yaml = Collections.unmodifiableMap(yaml);
    }

    @SuppressWarnings("unchecked")
    public void visitValuesRecursively(Object section, Consumer consumer) {
        if (isNode(section)) {
            ((Map)section).values().forEach(value -> visitValuesRecursively(value, consumer));
        } else if (isList(section)) {
            ((List)section).forEach(value -> visitValuesRecursively(value, consumer));
        } else {
            consumer.accept(section);
        }
    }

    public <T> T digRequiredValue(String ... sections) {
        return this.<T>digValue(sections).orElseThrow(sectionNotFoundException(sections));
    }

    public <T> Optional<T> digValue(String ... sections) {
        Optional<T> result = dig(sections);
        if (result.isPresent() && isNode(result.get())) {
            String path = String.join(KEY_SEPARATOR, sections);
            String message = "Property [" + path + "] is expected to be a value but this is a node.";
            throw new ConfigException(message);
        }
        return result;
    }

    public Map<String, Object> digRequiredNode(String ... sections) {
        return digNode(sections).orElseThrow(sectionNotFoundException(sections));
    }

    public Optional<Map<String, Object>> digNode(String ... sections) {
        Optional<Map<String, Object>> result = dig(sections);
        if (result.isPresent() && !isNode(result.get())) {
            String path = String.join(KEY_SEPARATOR, sections);
            String message = "Property [" + path + "] is expected to be a node but this is a value.";
            throw new ConfigException(message);
        }
        return result;
    }

    private Supplier<RuntimeException> sectionNotFoundException(String ... sections) {
        return () -> {
            String path = String.join(KEY_SEPARATOR, sections);
            String message = "Section [" + path + "] is required but absent in the configuration file";
            return new ConfigException(message);
        };
    }

    private <T> Optional<T> dig(String ... sections) {
        List<String> splittedSections =
            Arrays.stream(sections).
            flatMap(s -> Arrays.stream(s.split(KEY_SEPARATOR))).
            filter(s -> !s.isEmpty()).
            collect(Collectors.toList());

        if (splittedSections.isEmpty()) {
            throw new IllegalArgumentException("Name can not be an empty");
        }

        return dig(yaml, splittedSections, 0);
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> dig(Object root, List<String> sections, int index) {
        if (root == null) {
            return Optional.empty();
        }
        if (index >= sections.size()) {
            return Optional.of((T)root);
        }

        checkSectionIsNode(root, sections, index);
        return dig(readSection(root, sections.get(index)), sections, ++index);
    }

    private void checkSectionIsNode(Object section, List<String> sections, int index) {
        if (!isNode(section)) {
            String property = String.join(KEY_SEPARATOR, sections);
            String fragment = String.join(KEY_SEPARATOR, sections.subList(index, sections.size()));
            String message = "Can not read property [" + property + "], because [" + fragment
                    + "] is expected to be a node but this is a value, so can not be expanded";
            throw new ConfigException(message);
        }
    }

    private Object readSection(Object node, String section) {
        return ((Map)node).get(section);
    }

    private boolean isNode(Object object) {
        return object instanceof Map;
    }

    private boolean isList(Object object) {
        return object instanceof List;
    }
}
