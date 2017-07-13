package com.github.vkorobkov.jfixtures.instructions;

public interface InstructionVisitor {
    default void visit(CleanTable cleanTable) {}

    void visit(InsertRow insertRow);
}