package com.github.vkorobkov.jfixtures.instructions;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@NoArgsConstructor
@Getter
public class CustomSql implements Instruction {
    private static final String TABLE_NAME_PLACEHOLDER = "$TABLE_NAME";
    @XmlAttribute
    private static final String TYPE = "CustomSql";
    @XmlValue
    private String instruction;

    public CustomSql(String table, String instruction) {
        this.instruction = instruction.replace(TABLE_NAME_PLACEHOLDER, table);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
