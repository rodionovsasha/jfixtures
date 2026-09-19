package io.github.rodionovsasha.jfixtures.processor;

import io.github.rodionovsasha.jfixtures.IntId;
import io.github.rodionovsasha.jfixtures.UuidId;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        List<Instruction> instructions = new ArrayList<>();

        for (Row source : table.getRows()) {
            Row row = Row.of(source.getName(), baseColumns).columns(source.getColumns());
            Instruction insert = processRow(table.getName(), row, pkColumnName, rowsByPrimaryKey);
            instructions.add(insert);
            instructions.addAll(processManyToMany(table.getName(), row, (InsertRow)insert));
        }
        return instructions;
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
            Object identifier = table.shouldGenerateUuidPk()
                    ? UuidId.one(row.getName()).toString()
                    : IntId.one(row.getName());
            var id = Value.of(identifier);
            result.put(table.getPkColumnName(), id);
        }
        row.getColumns().forEach((name, value) -> {
            var polymorphic = context.getConfig().polymorphicReference(tableName, name);
            if (polymorphic.isPresent()) {
                addPolymorphicValues(result, tableName, row.getName(), name, value, polymorphic.get());
            } else if (context.getConfig().manyToMany(tableName, name).isEmpty()) {
                if (value.getValue() instanceof Collection<?>) {
                    String message = "List value is only supported for a configured many-to-many association ["
                            + tableName + "." + name + "]";
                    throw new ProcessorException(message);
                }
                result.put(name, columnProcessor.column(tableName, row.getName(), name, value));
            }
        });
        return result;
    }

    private void addPolymorphicValues(
            Map<String, Value> result,
            String table,
            String row,
            String column,
            Value value,
            Root.PolymorphicReference reference
    ) {
        String text = String.valueOf(value.getValue());
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("^(.+?) \\(([^()]+)\\)$").matcher(text);
        if (!matcher.matches()) {
            throw new ProcessorException("Polymorphic value [" + table + "." + row + "." + column
                    + "] must have the form [label (Type)]");
        }
        String label = matcher.group(1);
        String type = matcher.group(2);
        String targetTable = reference.types().get(type);
        if (targetTable == null) {
            throw new ProcessorException("Polymorphic type [" + type + "] is not configured for ["
                    + table + "." + column + "]");
        }
        result.put(reference.idColumn(), columnProcessor.reference(table, targetTable, label, null));
        result.put(reference.typeColumn(), Value.of(type));
    }

    private List<Instruction> processManyToMany(String table, Row row, InsertRow source) {
        List<Instruction> result = new ArrayList<>();
        row.getColumns().forEach((column, value) -> context.getConfig().manyToMany(table, column)
                .ifPresent(association -> addManyToManyRows(result, table, row, source, column, value, association)));
        return result;
    }

    private void addManyToManyRows(
            List<Instruction> result,
            String table,
            Row row,
            InsertRow source,
            String column,
            Value value,
            Root.ManyToMany association
    ) {
        if (!(value.getValue() instanceof Collection<?> labels)) {
            throw new ProcessorException(
                    "Many-to-many association [" + table + "." + column + "] must be a list of row labels"
            );
        }
        String sourcePk = context.getConfig().table(table).getPkColumnName();
        Value sourceId = source.getValues().get(sourcePk);
        if (sourceId == null) {
            throw new ProcessorException(
                    "Source primary key [" + table + "." + row.getName() + "." + sourcePk + "] is not found"
            );
        }
        Set<String> uniqueLabels = new HashSet<>();
        for (Object rawLabel : labels) {
            if (!(rawLabel instanceof String label)) {
                throw new ProcessorException(
                        "Many-to-many association [" + table + "." + column + "] contains a non-string label"
                );
            }
            if (!uniqueLabels.add(label)) {
                throw new ProcessorException("Duplicate many-to-many association [" + table + "." + row.getName()
                        + "." + column + "] for label [" + label + "]");
            }
            Value targetId = columnProcessor.reference(table, association.targetTable(), label, null);
            Map<String, Value> values = new LinkedHashMap<>();
            values.put(association.sourceColumn(), sourceId);
            values.put(association.targetColumn(), targetId);
            result.add(new InsertRow(association.joinTable(), row.getName() + "_" + label, values));
        }
    }
}
