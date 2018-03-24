package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.IntId;
import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.CustomSql;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.Fixture;
import com.github.vkorobkov.jfixtures.loader.Row;
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

    public Processor(Map<String, Fixture> fixtures, Root config) {
        this.context = new Context(fixtures, config);
        this.columnProcessor = new ColumnProcessor(context, this::processFixture);
    }

    public List<Instruction> process() {
        context.getFixtures().values().forEach(this::processFixture);
        return context.getInstructions();
    }

    private void processFixture(Fixture fixture) {
        context.getCircularPreventer().doInStack(fixture.name,  () -> {
            if (context.getCompletedFixtures().add(fixture.name)) {
                handleFixtureInstructions(fixture);
            }
        });
    }

    private void handleFixtureInstructions(Fixture fixture) {
        val tableName = fixture.name;
        val table = context.getConfig().table(tableName);

        log.info("Processing table '" + tableName + "'");
        List<Instruction> fixtureInstructions = new ArrayList<>();

        fixtureInstructions.addAll(table.getBeforeCleanup().stream()
                .map(s -> customSql(tableName, s)).collect(Collectors.toList()));

        fixtureInstructions.add(new CleanTable(tableName, table.getCleanMethod()));

        fixtureInstructions.addAll(table.getBeforeInserts().stream()
                .map(s -> customSql(tableName, s)).collect(Collectors.toList()));

        fixtureInstructions.addAll(processRows(fixture));

        fixtureInstructions.addAll(table.getAfterInserts().stream()
                .map(s -> customSql(tableName, s)).collect(Collectors.toList()));
        context.getInstructions().addAll(fixtureInstructions);
    }

    private Instruction customSql(String table, String instruction) {
        return new CustomSql(table, instruction);
    }

    private List<Instruction> processRows(Fixture fixture) {
        val baseColumns = context.getConfig().table(fixture.name).getDefaultColumns();
        return fixture.getRows().stream()
            .map(row -> row.withBaseColumns(baseColumns))
            .map(row -> processRow(fixture.name, row))
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
