package com.github.vkorobkov.jfixtures.fluent;

import com.github.vkorobkov.jfixtures.config.Config;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.FixturesLoader;
import com.github.vkorobkov.jfixtures.processor.Processor;
import com.github.vkorobkov.jfixtures.sql.Appender;
import com.github.vkorobkov.jfixtures.sql.Sql;
import com.github.vkorobkov.jfixtures.sql.SqlBridge;
import com.github.vkorobkov.jfixtures.sql.appenders.FileAppender;
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender;
import com.github.vkorobkov.jfixtures.util.WithResource;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;

@RequiredArgsConstructor
public class JFixturesResultImpl implements JFixturesResult {
    private final String fixturesFolder;
    private final Sql sql;

    private List<Instruction> instructions;

    @Override
    public String asString() {
        return applyAppender(new StringAppender()).toString();
    }

    @Override
    public void toFile(String name) {
        WithResource.touch(() -> new FileAppender(name), this::applyAppender);
    }

    private<T extends Appender> T applyAppender(T appender) {
        new SqlBridge(sql, appender).apply(getInstructions());
        return appender;
    }

    private List<Instruction> getInstructions() {
        if (instructions == null) {
            val fixtures = new FixturesLoader(fixturesFolder).load();
            val config = new Config(fixturesFolder);
            instructions = new Processor(fixtures, config).process();
        }
        return instructions;
    }
}
