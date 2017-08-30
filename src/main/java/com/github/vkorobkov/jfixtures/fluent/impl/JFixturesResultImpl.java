package com.github.vkorobkov.jfixtures.fluent.impl;

import com.github.vkorobkov.jfixtures.config.ConfigLoader;
import com.github.vkorobkov.jfixtures.fluent.JFixturesResult;
import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.CustomSql;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.FixturesLoader;
import com.github.vkorobkov.jfixtures.processor.Processor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@NoArgsConstructor
@XmlRootElement(name = "instructions")
abstract class JFixturesResultImpl implements JFixturesResult {
    private String fixturesFolder;

    @Setter
    @XmlElements({
            @XmlElement(name = "instruction", type = CleanTable.class),
            @XmlElement(name = "instruction", type = CustomSql.class),
            @XmlElement(name = "instruction", type = InsertRow.class)
    })
    private List<Instruction> instructions;

    JFixturesResultImpl(final String fixturesFolder) {
        this.fixturesFolder = fixturesFolder;
    }

    List<Instruction> getInstructions() {
        if (instructions == null) {
            val config = new ConfigLoader(fixturesFolder).load();
            val fixtures = new FixturesLoader(fixturesFolder, config).load();
            instructions = new Processor(fixtures, config).process();
        }
        return instructions;
    }
}
