package io.github.rodionovsasha.jfixtures.config.structure;

import io.github.rodionovsasha.jfixtures.config.yaml.Node;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class Section {
    @Getter
    private final Node node;
}
