package io.github.rodionovsasha.jfixtures.result;

import io.github.rodionovsasha.jfixtures.instructions.Instruction;
import io.github.rodionovsasha.jfixtures.instructions.InstructionVisitor;
import io.github.rodionovsasha.jfixtures.sql.Sql;
import io.github.rodionovsasha.jfixtures.sql.SqlType;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;
import java.sql.Connection;
import javax.sql.DataSource;

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

    public Result apply(Connection connection) {
        toSql99().apply(connection);
        return this;
    }

    public Result apply(DataSource dataSource) {
        toSql99().apply(dataSource);
        return this;
    }

}
