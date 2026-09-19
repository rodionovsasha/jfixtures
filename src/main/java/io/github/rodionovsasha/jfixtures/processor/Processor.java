package io.github.rodionovsasha.jfixtures.processor;

import io.github.rodionovsasha.jfixtures.IntId;
import io.github.rodionovsasha.jfixtures.config.structure.Root;
import io.github.rodionovsasha.jfixtures.domain.Row;
import io.github.rodionovsasha.jfixtures.domain.Table;
import io.github.rodionovsasha.jfixtures.domain.Value;
import io.github.rodionovsasha.jfixtures.instructions.CleanTable;
import io.github.rodionovsasha.jfixtures.instructions.CustomSql;
import io.github.rodionovsasha.jfixtures.instructions.InsertRow;
import io.github.rodionovsasha.jfixtures.instructions.Instruction;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Processor {
    private final ColumnProcessor columnProcessor;
    private final Context context;

    public Processor(Collection<Table> tables, Root config) {
        this.context = new Context(tables, config);
        this.columnProcessor = new ColumnProcessor(context, this::processTable);
    }

    public List<Instruction> process() {
        processConfiguredCleanTables();
        context.getTables().values().forEach(this::processTable);
        return context.getInstructions();
    }

    private void processConfiguredCleanTables() {
        context.getConfig().getCleanTables().stream()
                .filter(context.getCleanedTables()::add)
                .forEach(this::cleanConfiguredTable);
    }

    private void cleanConfiguredTable(String tableName) {
        var config = context.getConfig().table(tableName);
        addCustomSql(context.getInstructions(), tableName, config.getBeforeCleanup());
        context.getInstructions().add(new CleanTable(tableName, config.getCleanMethod()));
    }

    private void processTable(Table table) {
        context.getCircularPreventer().doInStack(table.getName(), () -> {
            if (context.getCompletedTables().add(table.getName())) {
                handleTableInstructions(table);
            }
        });
    }

    private void handleTableInstructions(Table table) {
        var tableName = table.getName();
        var config = context.getConfig().table(tableName);

        log.info("Processing table '{}'", tableName);

        List<Instruction> instructions = new ArrayList<>();
        if (context.getCleanedTables().add(tableName)) {
            addCustomSql(instructions, tableName, config.getBeforeCleanup());
            instructions.add(new CleanTable(tableName, config.getCleanMethod()));
        }

        addCustomSql(instructions, tableName, config.getBeforeInserts());

        instructions.addAll(processRows(table));

        addCustomSql(instructions, tableName, config.getAfterInserts());
        context.getInstructions().addAll(instructions);
    }

    private void addCustomSql(List<Instruction> instructions, String table, Collection<String> statements) {
        statements.forEach(statement -> instructions.add(new CustomSql(table, statement)));
    }

    private List<Instruction> processRows(Table table) {
        var config = context.getConfig();
        var baseColumns = config.table(table.getName()).getDefaultColumns();
        var pkColumnName = config.table(table.getName()).getPkColumnName();
        Map<Value, String> rowsByPrimaryKey = new LinkedHashMap<>();

        return table.getRows().stream()
                .map(row -> Row.of(row.getName(), baseColumns).columns(row.getColumns()))
                .map(row -> processRow(table.getName(), row, pkColumnName, rowsByPrimaryKey))
                .collect(Collectors.toList());
    }

    private Instruction processRow(
            String tableName,
            Row row,
            String pkColumnName,
            Map<Value, String> rowsByPrimaryKey
    ) {
        Map<String, Value> rowValues = extractRowValues(tableName, row);
        validatePrimaryKey(tableName, row, pkColumnName, rowValues, rowsByPrimaryKey);
        Instruction result = new InsertRow(tableName, row.getName(), rowValues);
        result.accept(context.getRowsIndex());
        return result;
    }

    private void validatePrimaryKey(
            String tableName,
            Row row,
            String pkColumnName,
            Map<String, Value> rowValues,
            Map<Value, String> rowsByPrimaryKey
    ) {
        Value primaryKey = rowValues.get(pkColumnName);
        if (primaryKey == null) {
            return;
        }

        String previousRow = rowsByPrimaryKey.putIfAbsent(primaryKey, row.getName());
        if (row.getColumns().containsKey(pkColumnName) && previousRow != null) {
            String message = String.format(
                    "Duplicate primary key [%s=%s] in table [%s]: rows [%s] and [%s] define the same value",
                    pkColumnName, primaryKey.getValue(), tableName, previousRow, row.getName()
            );
            throw new ProcessorException(message);
        }
    }

    private Map<String, Value> extractRowValues(String tableName, Row row) {
        Map<String, Value> result = new LinkedHashMap<>(row.getColumns().size() + 1);
        var table = context.getConfig().table(tableName);
        if (table.shouldAutoGeneratePk()) {
            var id = Value.of(IntId.one(row.getName()));
            result.put(table.getPkColumnName(), id);
        }
        row.getColumns().forEach((name, value) -> {
            value = columnProcessor.column(tableName, row.getName(), name, value);
            result.put(name, value);
        });
        return result;
    }
}
