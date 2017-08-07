package com.github.vkorobkov.jfixtures.config.structure;

import com.github.vkorobkov.jfixtures.config.yaml.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Section {
    @Getter
    private final Node node;
}
