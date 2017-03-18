package com.github.vkorobkov.jfixtures.sql;

import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.instructions.InstructionVisitor;
import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class SqlBridge implements InstructionVisitor {
    private final Sql sql;
    private final Appender appender;

    public void apply(Collection<Instruction> instructions) {
        instructions.forEach(instruction -> instruction.accept(this));
    }

    @Override
    public void visit(CleanTable cleanTable) {
        sql.cleanTable(appender, cleanTable);
    }

    @Override
    public void visit(InsertRow insertRow) {
        sql.insertRow(appender, insertRow);
    }
}
