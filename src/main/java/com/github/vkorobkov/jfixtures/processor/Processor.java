package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.Fixture;
import com.github.vkorobkov.jfixtures.loader.FixtureRow;
import com.github.vkorobkov.jfixtures.loader.FixtureValue;
import com.github.vkorobkov.jfixtures.processor.sequence.IncrementalSequence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Processor {
    static final String PK_COLUMN_NAME = "id";
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
        List<Instruction> fixtureInstructions = new ArrayList<>();
        fixtureInstructions.add(cleanupTable(fixture));
        fixtureInstructions.addAll(processRows(fixture));
        context.getInstructions().addAll(fixtureInstructions);
    }

    private Instruction cleanupTable(Fixture fixture) {
        return new CleanTable(fixture.name);
    }

    private List<Instruction> processRows(Fixture fixture) {
        String tableName = fixture.name;
        context.getSequenceRegistry().put(tableName, new IncrementalSequence());
        return fixture.getRows().stream().map(row -> processRow(tableName, row)).collect(Collectors.toList());
    }

    private Instruction processRow(String tableName, FixtureRow row) {
        Instruction result =  new InsertRow(tableName, row.getName(), extractRowValues(tableName, row));
        result.accept(context.getRowsIndex());
        return result;
    }

    private Map<String, FixtureValue> extractRowValues(String tableName, FixtureRow row) {
        Map<String, FixtureValue> result = new LinkedHashMap<>(row.getColumns().size() + 1);
        result.put(PK_COLUMN_NAME, context.getSequenceRegistry().nextValue(tableName, row.getName()));
        row.getColumns().forEach((name, value) -> {
            value = columnProcessor.column(tableName, row.getName(), name, value);
            result.put(name, value);
        });
        return result;
    }
}
