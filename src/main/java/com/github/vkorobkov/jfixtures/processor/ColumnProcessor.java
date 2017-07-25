package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.config.Config;
import com.github.vkorobkov.jfixtures.config.TablesConfig;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.loader.Fixture;
import com.github.vkorobkov.jfixtures.loader.FixtureValue;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.Consumer;

@AllArgsConstructor
class ColumnProcessor {
    private final Context context;
    private final Consumer<Fixture> dependencyResolver;

    FixtureValue column(String table, String rowName, String column, FixtureValue value) {
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

    private FixtureValue referredColumn(String table, String referredTable, FixtureValue value) {
        if (!table.equals(referredTable)) {
            processDependentFixture(referredTable);
        }

        val referredRow = referredRow(referredTable, value);

        //todo: so far we always have PK so that check does not matter for a while
        /*if (!referredRow.values.containsKey(Processor.PK_COLUMN_NAME)) {
            String columnPath = String.join(".", referredTable, String.valueOf(value.value), Processor.PK_COLUMN_NAME);
            String message = "Referred column [" + columnPath + "] is not found";
            throw new ProcessorException(message);
        }*/

        return referredRow.getValues().get(getTablesConfig().getCustomColumnForPk(referredTable));
    }

    private InsertRow referredRow(String table, FixtureValue value) {
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
        val referredFixture = context.getFixtures().get(referredTable);
        if (referredFixture == null) {
            String message = "Referred table [" + referredTable + "] is not found";
            throw new ProcessorException(message);
        }
        dependencyResolver.accept(referredFixture);
    }

    private Config getConfig() {
        return context.getConfig();
    }

    private TablesConfig getTablesConfig() {
        return new TablesConfig(getConfig().getYamlConfig());
    }
}
