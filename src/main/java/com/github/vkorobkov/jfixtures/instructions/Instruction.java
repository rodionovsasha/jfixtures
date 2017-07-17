package com.github.vkorobkov.jfixtures.instructions;

@FunctionalInterface
public interface Instruction {
    void accept(InstructionVisitor visitor);
}
