package io.github.rodionovsasha.jfixtures.result;

import io.github.rodionovsasha.jfixtures.instructions.Instruction;
import io.github.rodionovsasha.jfixtures.sql.Appender;
import io.github.rodionovsasha.jfixtures.sql.Sql;
import io.github.rodionovsasha.jfixtures.sql.SqlBridge;
import io.github.rodionovsasha.jfixtures.sql.appenders.FileAppender;
import io.github.rodionovsasha.jfixtures.sql.appenders.StringAppender;
import io.github.rodionovsasha.jfixtures.util.WithResource;
import lombok.Getter;

import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;


@Getter
public class SqlResult implements StringResult {
    private final Collection<Instruction> instructions;
    private final Sql sql;

    public SqlResult(Collection<Instruction> instructions, Sql sql) {
        this.instructions = unmodifiableCollection(instructions);
        this.sql = sql;
    }

    @Override
    public String toString() {
        return applyAppender(new StringAppender()).toString();
    }

    @Override
    public void toFile(String name) {
        WithResource.touch(() -> new FileAppender(name), this::applyAppender);
    }

    public <T extends Appender> T applyAppender(T appender) {
        createSqlBridge(appender).apply(instructions);
        return appender;
    }

    private <T extends Appender> SqlBridge createSqlBridge(T appender) {
        return new SqlBridge(sql, appender);
    }
}
