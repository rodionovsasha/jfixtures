package com.github.vkorobkov.jfixtures.result;

import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.instructions.InstructionVisitor;
import com.github.vkorobkov.jfixtures.sql.Sql;
import com.github.vkorobkov.jfixtures.sql.SqlType;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

@Getter
public class Result {
    private final Collection<Instruction> instructions;

    public Result(Collection<Instruction> instructions) {
        this.instructions = Collections.unmodifiableCollection(instructions);
    }

    public void visit(InstructionVisitor visitor) {
        instructions.forEach(instruction -> instruction.accept(visitor));
    }

    public SqlResult toSql99() {
        return toSql(SqlType.SQL99);
    }

    public SqlResult toMySql() {
        return toSql(SqlType.MYSQL);
    }

    public SqlResult toMicrosoftSql() {
        return toSql(SqlType.MICROSOFT_SQL);
    }

    public SqlResult toSql(SqlType type) {
        return toSql(type.getSqlDialect());
    }

    public SqlResult toSql(Sql sql) {
        return new SqlResult(instructions, sql);
    }

    public XmlResult toXml() {
        return new XmlResult(instructions);
    }
}
