package com.github.rodionovsasha.jfixtures.instructions;

@FunctionalInterface
public interface InstructionVisitor {
    default void visit(CleanTable cleanTable) {
    }

    void visit(InsertRow insertRow);

    default void visit(CustomSql customSql) {
    }
}
