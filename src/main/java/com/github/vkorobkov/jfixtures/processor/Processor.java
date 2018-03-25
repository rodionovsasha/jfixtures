package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.IntId;
import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.CustomSql;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.Row;
import com.github.vkorobkov.jfixtures.loader.Table;
import com.github.vkorobkov.jfixtures.loader.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Processor {
    private final ColumnProcessor columnProcessor;
    private final Context context;

    public Processor(Map<String, Table> tables, Root config) {
        this.context = new Context(tables, config);
        this.columnProcessor = new ColumnProcessor(context, this::processTable);
    }

    public List<Instruction> process() {
        context.getTables().values().forEach(this::processTable);
        return context.getInstructions();
    }

    private void processTable(Table table) {
        context.getCircularPreventer().doInStack(table.name,  () -> {
            if (context.getCompletedTables().add(table.name)) {
                handleTableInstructions(table);
            }
        });
    }

    private void handleTableInstructions(Table table) {
        val tableName = table.name;
        val config = context.getConfig().table(tableName);

        log.info("Processing table '" + tableName + "'");
        List<Instruction> instructions = new ArrayList<>();

        instructions.addAll(config.getBeforeCleanup().stream()
                .map(s -> customSql(tableName, s)).collect(Collectors.toList()));

        instructions.add(new CleanTable(tableName, config.getCleanMethod()));

        instructions.addAll(config.getBeforeInserts().stream()
                .map(s -> customSql(tableName, s)).collect(Collectors.toList()));

        instructions.addAll(processRows(table));

        instructions.addAll(config.getAfterInserts().stream()
                .map(s -> customSql(tableName, s)).collect(Collectors.toList()));
        context.getInstructions().addAll(instructions);
    }

    private Instruction customSql(String table, String instruction) {
        return new CustomSql(table, instruction);
    }

    private List<Instruction> processRows(Table table) {
        val baseColumns = context.getConfig().table(table.name).getDefaultColumns();
        return table.getRows().stream()
            .map(row -> row.withBaseColumns(baseColumns))
            .map(row -> processRow(table.name, row))
            .collect(Collectors.toList());
    }

    private Instruction processRow(String tableName, Row row) {
        Instruction result =  new InsertRow(tableName, row.getName(), extractRowValues(tableName, row));
        result.accept(context.getRowsIndex());
        return result;
    }

    private Map<String, Value> extractRowValues(String tableName, Row row) {
        Map<String, Value> result = new LinkedHashMap<>(row.getColumns().size() + 1);
        val table = context.getConfig().table(tableName);
        if (table.shouldAutoGeneratePk()) {
            val id = new Value(IntId.one(row.getName()));
            result.put(table.getPkColumnName(), id);
        }
        row.getColumns().forEach((name, value) -> {
            value = columnProcessor.column(tableName, row.getName(), name, value);
            result.put(name, value);
        });
        return result;
    }
}
