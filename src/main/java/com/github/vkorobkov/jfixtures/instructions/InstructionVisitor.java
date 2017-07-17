package com.github.vkorobkov.jfixtures.instructions;

@FunctionalInterface
public interface InstructionVisitor {
    default void visit(CleanTable cleanTable) {}

    void visit(InsertRow insertRow);
}
