package com.github.vkorobkov.jfixtures.instructions;

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod;

@FunctionalInterface
public interface InstructionVisitor {
    default void visit(CleanTable cleanTable, CleanMethod cleanMethod) {
    }

    void visit(InsertRow insertRow);

    default void visit(CustomSql customSql) {
    }
}
