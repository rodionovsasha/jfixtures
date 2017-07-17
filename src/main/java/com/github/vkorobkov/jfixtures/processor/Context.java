package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.config.Config;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.Fixture;
import com.github.vkorobkov.jfixtures.processor.sequence.SequenceRegistry;
import lombok.Getter;

import java.util.*;

@Getter
class Context {
    private final SequenceRegistry sequenceRegistry = new SequenceRegistry();
    private final List<Instruction> instructions = new ArrayList<>();
    private final RowsIndex rowsIndex = new RowsIndex();
    private final Set<String> completedFixtures = new HashSet<>();
    private final CircularPreventer circularPreventer = new CircularPreventer();
    private final Map<String, Fixture> fixtures;
    private final Config config;

    Context(Map<String, Fixture> fixtures, Config config) {
        this.fixtures = Collections.unmodifiableMap(fixtures);
        this.config = config;
    }
}
