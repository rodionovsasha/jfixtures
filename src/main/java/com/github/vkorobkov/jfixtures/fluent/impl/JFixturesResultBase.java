package com.github.vkorobkov.jfixtures.fluent.impl;

import com.github.vkorobkov.jfixtures.config.ConfigLoaderOld;
import com.github.vkorobkov.jfixtures.fluent.JFixturesResult;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.DirectoryLoader;
import com.github.vkorobkov.jfixtures.processor.Processor;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
abstract class JFixturesResultBase implements JFixturesResult {
    private String fixturesFolder;

    List<Instruction> getInstructions() {
        val fixtures = new DirectoryLoader(fixturesFolder).load();
        val config = ConfigLoaderOld.load(fixturesFolder);
        return new Processor(fixtures, config).process();
    }
}
