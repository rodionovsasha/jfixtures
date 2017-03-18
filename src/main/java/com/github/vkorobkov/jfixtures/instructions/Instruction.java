package com.github.vkorobkov.jfixtures.instructions;

public interface Instruction {
    void accept(InstructionVisitor visitor);
}
