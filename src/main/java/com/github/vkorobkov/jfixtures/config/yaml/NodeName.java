package com.github.vkorobkov.jfixtures.config.yaml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public final class NodeName {
    private final List<String> sections;

    private NodeName(List<String> sections) {
        this.sections = Collections.unmodifiableList(sections);
    }

    public static NodeName root() {
        return new NodeName(Collections.emptyList());
    }

    public NodeName sub(String ... names) {
        List<String> newSections = new ArrayList<>(sections.size() + names.length);
        newSections.addAll(sections);
        newSections.addAll(Arrays.asList(names));
        return new NodeName(newSections);
    }

    @Override
    public String toString() {
        return String.join(":", sections);
    }

    public boolean isRoot() {
        return sections.isEmpty();
    }
}
