package com.github.vkorobkov.jfixtures.instructions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CleanTable implements Instruction {
    @Getter
    private final String table;

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
