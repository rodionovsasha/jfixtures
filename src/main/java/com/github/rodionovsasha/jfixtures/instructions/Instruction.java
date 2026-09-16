package com.github.rodionovsasha.jfixtures.instructions;

@FunctionalInterface
public interface Instruction {
    void accept(InstructionVisitor visitor);
}
