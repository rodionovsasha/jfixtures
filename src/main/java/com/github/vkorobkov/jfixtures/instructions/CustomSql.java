package com.github.vkorobkov.jfixtures.instructions;

import lombok.Getter;

@Getter
public class CustomSql implements Instruction {
    private static final String TABLE_NAME_PLACEHOLDER = "$TABLE_NAME";

    private final String table;
    private final String instruction;

    public CustomSql(String table, String instruction) {
        this.table = table;
        this.instruction = instruction.replace(TABLE_NAME_PLACEHOLDER, table);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
