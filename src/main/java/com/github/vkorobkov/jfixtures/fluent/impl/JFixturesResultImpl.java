package com.github.vkorobkov.jfixtures.fluent.impl;

import com.github.vkorobkov.jfixtures.config.ConfigLoader;
import com.github.vkorobkov.jfixtures.fluent.JFixturesResult;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.FixturesLoader;
import com.github.vkorobkov.jfixtures.processor.Processor;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;

@RequiredArgsConstructor
abstract class JFixturesResultImpl implements JFixturesResult {
    private final String fixturesFolder;

    private List<Instruction> instructions;

    List<Instruction> getInstructions() {
        if (instructions == null) {
            val config = new ConfigLoader(fixturesFolder).load();
            val fixtures = new FixturesLoader(fixturesFolder, config).load();
            instructions = new Processor(fixtures, config).process();
        }
        return instructions;
    }
}
