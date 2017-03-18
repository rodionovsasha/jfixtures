package com.github.vkorobkov.jfixtures.instructions;

public class CleanTable implements Instruction {
    public final String table;

    public CleanTable(String table) {
        this.table = table;
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
