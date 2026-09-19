package io.github.rodionovsasha.jfixtures.processor;

import io.github.rodionovsasha.jfixtures.instructions.CleanTable;
import io.github.rodionovsasha.jfixtures.instructions.CustomSql;
import io.github.rodionovsasha.jfixtures.instructions.InsertRow;
import io.github.rodionovsasha.jfixtures.instructions.Instruction;
import io.github.rodionovsasha.jfixtures.instructions.InstructionVisitor;
import io.github.rodionovsasha.jfixtures.sql.Sql;
import io.github.rodionovsasha.jfixtures.sql.appenders.StringAppender;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

/** Applies already compiled instructions through a caller-provided JDBC connection. */
public final class JdbcBridge implements InstructionVisitor {
    private final Sql sql;
    private final Connection connection;

    public JdbcBridge(Sql sql, Connection connection) {
        this.sql = sql;
        this.connection = connection;
    }

    public void apply(Collection<Instruction> instructions) {
        instructions.forEach(instruction -> instruction.accept(this));
    }

    @Override
    public void visit(CleanTable instruction) {
        execute(appender -> sql.cleanTable(appender, instruction));
    }

    @Override
    public void visit(InsertRow instruction) {
        execute(appender -> sql.insertRow(appender, instruction));
    }

    @Override
    public void visit(CustomSql instruction) {
        execute(appender -> sql.addCustomSql(appender, instruction));
    }

    private void execute(SqlRenderer renderer) {
        StringAppender appender = new StringAppender();
        renderer.render(appender);
        String statement = appender.toString().trim();
        if (statement.isEmpty()) {
            return;
        }
        try (Statement jdbcStatement = connection.createStatement()) {
            jdbcStatement.execute(statement);
        } catch (SQLException cause) {
            throw new JdbcException("Unable to apply fixture SQL: " + statement, cause);
        }
    }

    @FunctionalInterface
    private interface SqlRenderer {
        void render(StringAppender appender);
    }
}
