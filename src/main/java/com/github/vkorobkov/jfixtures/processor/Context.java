package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.config.Config;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.Fixture;
import com.github.vkorobkov.jfixtures.processor.sequence.SequenceRegistry;

import java.util.*;

class Context {
    final SequenceRegistry sequenceRegistry = new SequenceRegistry();
    final List<Instruction> instructions = new ArrayList<>();
    final RowsIndex rowsIndex = new RowsIndex();
    final Set<String> completedFixtures = new HashSet<>();
    final CircularPreventer circularPreventer = new CircularPreventer();
    final Map<String, Fixture> fixtures;
    final Config config;

    Context(Map<String, Fixture> fixtures, Config config) {
        this.fixtures = Collections.unmodifiableMap(fixtures);
        this.config = config;
    }
}
