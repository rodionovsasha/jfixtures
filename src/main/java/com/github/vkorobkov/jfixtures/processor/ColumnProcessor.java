package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.loader.Table;
import com.github.vkorobkov.jfixtures.loader.Value;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.Consumer;

@AllArgsConstructor
class ColumnProcessor {
    private final Context context;
    private final Consumer<Table> dependencyResolver;

    Value column(String table, String rowName, String column, Value value) {
        try {
            return getConfig().referredTable(table, column)
                    .map(referredTable -> referredColumn(table, referredTable, value))
                    .orElse(value);
        } catch (ProcessorException cause) {
            String columnPath = String.join(".", table, rowName, column);
            String message = "Error processing [" + columnPath + "]. Root cause:\n" + cause.getMessage();
            throw new ProcessorException(message);
        }
    }

    private Value referredColumn(String table, String referredTable, Value value) {
        if (!table.equals(referredTable)) {
            processDependentFixture(referredTable);
        }

        val referredRowValues = referredRow(referredTable, value).getValues();
        val referredPk = getConfig().table(referredTable).getPkColumnName();

        if (!referredRowValues.containsKey(referredPk)) {
            String columnPath = String.join(".", referredTable, String.valueOf(value.getValue()), referredPk);
            String message = "Referred column [" + columnPath + "] is not found";
            throw new ProcessorException(message);
        }

        return referredRowValues.get(referredPk);
    }

    private InsertRow referredRow(String table, Value value) {
        String rowName = String.valueOf(value.getValue());
        return context.getRowsIndex()
                .read(table, rowName)
                .orElseThrow(() -> {
                    String rowPath = String.join(".", table, rowName);
                    String message = "Referred row [" + rowPath + "] is not found";
                    return new ProcessorException(message);
                });
    }

    private void processDependentFixture(String referredTable) {
        val referredFixture = context.getTables().get(referredTable);
        if (referredFixture == null) {
            String message = "Referred table [" + referredTable + "] is not found";
            throw new ProcessorException(message);
        }
        dependencyResolver.accept(referredFixture);
    }

    private Root getConfig() {
        return context.getConfig();
    }
}
