package io.github.rodionovsasha.jfixtures.processor;

import io.github.rodionovsasha.jfixtures.config.structure.Root;
import io.github.rodionovsasha.jfixtures.domain.Table;
import io.github.rodionovsasha.jfixtures.domain.Value;
import io.github.rodionovsasha.jfixtures.instructions.InsertRow;
import lombok.AllArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Consumer;

@AllArgsConstructor
class ColumnProcessor {
    private static final int COLUMN_GROUP = 3;
    private static final Pattern INLINE_REFERENCE = Pattern.compile(
            "^([A-Za-z_][A-Za-z0-9_.]*):([^:\\s]+)(?::([A-Za-z_][A-Za-z0-9_]*))?$"
    );
    private static final Pattern LABEL_ID = Pattern.compile("^\\$ID\\(([^()\\s]+)\\)$");
    private final Context context;
    private final Consumer<Table> dependencyResolver;

    Value column(String table, String rowName, String column, Value value) {
        try {
            Value interpolated = interpolateLabel(rowName, value);
            var inlineReference = inlineReference(interpolated);
            if (inlineReference != null) {
                return referredColumn(table, inlineReference.table(), inlineReference.row(), inlineReference.column());
            }
            return getConfig().foreignKey(table, column)
                    .map(reference -> referredColumn(
                            table,
                            reference.table(),
                            String.valueOf(interpolated.getValue()),
                            reference.column()
                    ))
                    .orElse(interpolated);
        } catch (ProcessorException cause) {
            String columnPath = String.join(".", table, rowName, column);
            String message = "Error processing [" + columnPath + "]. Root cause:\n" + cause.getMessage();
            throw new ProcessorException(message);
        }
    }

    private Value referredColumn(String table, String referredTable, String rowName, String referredColumn) {
        referredTable = context.resolveTableName(table, referredTable);
        if (!table.equals(referredTable)) {
            processDependentFixture(referredTable);
        }

        var referredRowValues = referredRow(referredTable, rowName).getValues();
        var primaryKeyColumns = getConfig().table(referredTable).getPkColumnNames();
        if (referredColumn == null && primaryKeyColumns.size() > 1) {
            throw new ProcessorException("Referred row [" + referredTable + "." + rowName
                    + "] has a composite primary key; configure a composite reference or name a column");
        }
        var referredPk = referredColumn == null ? primaryKeyColumns.get(0) : referredColumn;

        if (!referredRowValues.containsKey(referredPk)) {
            String columnPath = String.join(".", referredTable, rowName, referredPk);
            String message = "Referred column [" + columnPath + "] is not found";
            throw new ProcessorException(message);
        }

        return referredRowValues.get(referredPk);
    }

    InsertRow referredRow(String table, String rowName) {
        return context.getRowsIndex()
                .read(table, rowName)
                .orElseThrow(() -> {
                    String rowPath = String.join(".", table, rowName);
                    String message = "Referred row [" + rowPath + "] is not found";
                    return new ProcessorException(message);
                });
    }

    Value reference(String table, String referredTable, String rowName, String column) {
        return referredColumn(table, referredTable, rowName, column);
    }

    private void processDependentFixture(String referredTable) {
        var referredFixture = context.getTables().get(referredTable);
        if (referredFixture == null) {
            String message = "Referred table [" + referredTable + "] is not found";
            throw new ProcessorException(message);
        }
        dependencyResolver.accept(referredFixture);
    }

    private Root getConfig() {
        return context.getConfig();
    }

    private Value interpolateLabel(String rowName, Value value) {
        if (!(value.getValue() instanceof String text)) {
            return value;
        }
        Matcher identifier = LABEL_ID.matcher(text);
        if (identifier.matches()) {
            return Value.of(io.github.rodionovsasha.jfixtures.IntId.one(identifier.group(1)));
        }
        String result = text.replace("$LABEL", rowName);
        if (result.equals(text)) {
            return value;
        }
        return value.getType() == io.github.rodionovsasha.jfixtures.domain.ValueType.SQL
                ? Value.ofSql(result)
                : Value.ofText(result);
    }

    private InlineReference inlineReference(Value value) {
        if (!(value.getValue() instanceof String text)) {
            return null;
        }
        Matcher matcher = INLINE_REFERENCE.matcher(text);
        return matcher.matches()
                ? new InlineReference(matcher.group(1), matcher.group(2), matcher.group(COLUMN_GROUP))
                : null;
    }

    private record InlineReference(String table, String row, String column) { }
}
