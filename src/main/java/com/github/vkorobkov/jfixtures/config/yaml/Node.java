package com.github.vkorobkov.jfixtures.config.yaml;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Node {
    private final Optional<Object> content;
    private final NodeName name;

    public static Node emptyRoot() {
        return new Node(Optional.empty(), NodeName.root());
    }

    public static Node root(Object content) {
        return new Node(Optional.of(content), NodeName.root());
    }

    private static Node subNode(Node root, String name, Object content) {
        return new Node(Optional.ofNullable(content), root.name.sub(name));
    }

    public boolean hasChildren() {
        return !asMap().isEmpty();
    }

    public Stream<Node> children() {
        return asMap().entrySet().stream().map(entry -> {
           return subNode(this, entry.getKey(), entry.getValue());
        });
    }

    public Node dig(String ... names) {
        Node result = this;
        for (String name: names) {
            result = result.child(name);
        }
        return result;
    }

    public Node child(String name) {
        return subNode(this, name, asMap().get(name));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> optional() {
        return (Optional<T>) content;
    }

    @SuppressWarnings("unchecked")
    public <T> T required() {
        return (T)content.orElseThrow(() -> new NodeMissingException(name.toString()));
    }

    @Override
    public String toString() {
        return name.toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap() {
        return (Map<String, Object>)content
            .filter(c -> c instanceof Map)
            .orElse(Collections.emptyMap());
    }
}
