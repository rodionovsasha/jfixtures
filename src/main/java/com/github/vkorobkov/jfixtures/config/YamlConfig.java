package com.github.vkorobkov.jfixtures.config;

import com.github.vkorobkov.jfixtures.util.YmlUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class YamlConfig implements ConfigDigger {
    private final Map<String, Object> config;

    public YamlConfig(Path path) {
        this.config = loadConfig(path);
    }

    @Override
    public<T> Optional<T> digValue(String name) {
        Optional<T> result = dig(name);
        if (result.isPresent() && isNode(result.get())) {
            String message = "Property [" + name + "] is expected to be a value but this is a node.";
            throw new ConfigException(message);
        }
        return result;
    }

    private<T> Optional<T> dig(String name) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name can not be an empty string");
        }
        String[] sections = name.split(KEY_SEPARATOR);
        return dig(config, sections, 0);
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> dig(Object root, String[] sections, int index) {
        if (root == null) {
            return Optional.empty();
        }
        if (index >= sections.length) {
            return Optional.of((T)root);
        }

        checkSectionIsNode(root, sections, index);
        return dig(readSection(root, sections[index]), sections, ++index);
    }

    private void checkSectionIsNode(Object section, String[] sections, int index) {
        if (!isNode(section)) {
            String property = String.join(KEY_SEPARATOR, sections);
            String fragment = String.join(KEY_SEPARATOR, Arrays.copyOf(sections, index));
            String message = "Can not read property [" + property + "], because [" + fragment +
                    "] is expected to be a node but this is a value, so can not be expanded";
            throw new ConfigException(message);
        }
    }

    private Object readSection(Object node, String section) {
        return ((Map)node).get(section);
    }

    private boolean isNode(Object object) {
        return object instanceof Map;
    }

    private Map<String, Object> loadConfig(Path path) {
        try {
            return Collections.unmodifiableMap(YmlUtil.load(path));
        } catch (IOException exception) {
            String message = "Error loading configuration file: " + path;
            throw new ConfigException(message, exception);
        }
    }
}
