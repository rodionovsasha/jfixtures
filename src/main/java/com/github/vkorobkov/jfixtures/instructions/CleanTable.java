package com.github.vkorobkov.jfixtures.instructions;

import lombok.Getter;

@Getter
public class CleanTable implements Instruction {
    private final String table;

    public CleanTable(String table) {
        this.table = table;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
