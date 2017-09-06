package com.github.vkorobkov.jfixtures.instructions;

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CleanTable implements Instruction {
    @XmlAttribute
    private static final String TYPE = "CleanTable";
    @XmlAttribute
    private String table;
    @XmlAttribute
    private CleanMethod cleanMethod;

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
