package com.github.vkorobkov.jfixtures.instructions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomSql implements Instruction {
    private final String table;
    private final String instruction;

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
