package io.github.rodionovsasha.jfixtures.sql;

import io.github.rodionovsasha.jfixtures.instructions.CleanTable;
import io.github.rodionovsasha.jfixtures.instructions.CustomSql;
import io.github.rodionovsasha.jfixtures.instructions.InsertRow;
import io.github.rodionovsasha.jfixtures.instructions.Instruction;
import io.github.rodionovsasha.jfixtures.instructions.InstructionVisitor;
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

    @Override
    public void visit(CustomSql customSql) {
        sql.addCustomSql(appender, customSql);
    }
}
