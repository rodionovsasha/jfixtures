package com.github.vkorobkov.jfixtures.result;

import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.sql.Appender;
import com.github.vkorobkov.jfixtures.sql.Sql;
import com.github.vkorobkov.jfixtures.sql.SqlBridge;
import com.github.vkorobkov.jfixtures.sql.appenders.FileAppender;
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender;
import com.github.vkorobkov.jfixtures.util.WithResource;
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
