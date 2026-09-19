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
        this.context = new Context(FixtureTemplates.expand(tables, config), config);
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

        processRequiredTables(tableName, config.getRequires());

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
        var pkColumnNames = config.table(table.getName()).getPkColumnNames();
        Map<List<Value>, String> rowsByPrimaryKey = new LinkedHashMap<>();
        List<Instruction> instructions = new ArrayList<>();

        for (Row source : table.getRows()) {
            Row row = Row.of(source.getName(), baseColumns).columns(source.getColumns());
            row = addTimestamps(row, config.table(table.getName()));
            Instruction insert = processRow(table.getName(), row, pkColumnNames, rowsByPrimaryKey);
            instructions.add(insert);
            instructions.addAll(processManyToMany(table.getName(), row, (InsertRow)insert));
        }
        return instructions;
    }

    private void processRequiredTables(String tableName, List<String> requirements) {
        for (String requiredTable : requirements) {
            String resolved = context.resolveTableName(tableName, requiredTable);
            Table fixture = context.getTables().get(resolved);
            if (fixture == null) {
                throw new ProcessorException("Required table [" + resolved + "] is not found");
            }
            processTable(fixture);
        }
    }

    private Row addTimestamps(Row row, io.github.rodionovsasha.jfixtures.config.structure.tables.Tables table) {
        if (!table.shouldAddTimestamps()) {
            return row;
        }
        Object timestamp = table.getTimestampValue().orElse(Value.ofSql("CURRENT_TIMESTAMP"));
        for (String column : List.of("created_at", "created_on", "updated_at", "updated_on")) {
            if (!row.getColumns().containsKey(column)) {
                row = row.column(column, timestamp);
            }
        }
        return row;
    }

    private Instruction processRow(
            String tableName,
            Row row,
            List<String> pkColumnNames,
            Map<List<Value>, String> rowsByPrimaryKey
    ) {
        Map<String, Value> rowValues = extractRowValues(tableName, row);
        validatePrimaryKey(tableName, row, pkColumnNames, rowValues, rowsByPrimaryKey);
        Instruction result = new InsertRow(tableName, row.getName(), rowValues);
        result.accept(context.getRowsIndex());
        return result;
    }

    private void validatePrimaryKey(
            String tableName,
            Row row,
            List<String> pkColumnNames,
            Map<String, Value> rowValues,
            Map<List<Value>, String> rowsByPrimaryKey
    ) {
        List<Value> primaryKey = new ArrayList<>(pkColumnNames.size());
        for (String column : pkColumnNames) {
            Value value = rowValues.get(column);
            if (value == null) {
                if (pkColumnNames.size() == 1) {
                    return;
                }
                throw new ProcessorException("Primary key [" + tableName + "] is incomplete; missing column ["
                        + column + "]");
            }
            primaryKey.add(value);
        }

        String previousRow = rowsByPrimaryKey.putIfAbsent(primaryKey, row.getName());
        if (previousRow != null) {
            String key = formatPrimaryKey(pkColumnNames, primaryKey);
            String message = String.format(
                    "Duplicate primary key [%s] in table [%s]: rows [%s] and [%s] define the same value",
                    key, tableName, previousRow, row.getName()
            );
            throw new ProcessorException(message);
        }
    }

    private String formatPrimaryKey(List<String> columns, List<Value> values) {
        List<String> entries = new ArrayList<>(columns.size());
        for (int index = 0; index < columns.size(); index++) {
            entries.add(columns.get(index) + "=" + values.get(index).getValue());
        }
        return String.join(", ", entries);
    }

    private Map<String, Value> extractRowValues(String tableName, Row row) {
        Map<String, Value> result = new LinkedHashMap<>(row.getColumns().size() + 1);
        var table = context.getConfig().table(tableName);
        if (table.shouldAutoGeneratePk()) {
            for (String column : table.getPkColumnNames()) {
                String label = table.getPkColumnNames().size() == 1 ? row.getName() : row.getName() + "." + column;
                Object identifier = identifier(table, label);
                result.put(column, Value.of(identifier));
            }
        }
        row.getColumns().forEach((name, value) -> {
            var polymorphic = context.getConfig().polymorphicReference(tableName, name);
            if (polymorphic.isPresent()) {
                addPolymorphicValues(result, tableName, row.getName(), name, value, polymorphic.get());
            } else if (context.getConfig().compositeForeignKey(tableName, name).isPresent()) {
                addCompositeReferenceValues(result, tableName, row.getName(), name, value,
                        context.getConfig().compositeForeignKey(tableName, name).get());
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

    private Object identifier(io.github.rodionovsasha.jfixtures.config.structure.tables.Tables table, String label) {
        var generator = table.getIdGenerator().or(context.getConfig()::getIdGenerator);
        if (generator.isPresent()) {
            return IdentifierGenerator.generate(generator.get(), label);
        }
        return table.shouldGenerateUuidPk() ? UuidId.one(label).toString() : IntId.one(label);
    }

    private void addCompositeReferenceValues(
            Map<String, Value> result,
            String table,
            String row,
            String column,
            Value value,
            Root.CompositeForeignKey reference
    ) {
        if (!(value.getValue() instanceof String label) || label.isBlank()) {
            throw new ProcessorException("Composite reference [" + table + "." + row + "." + column
                    + "] must be a row label");
        }
        String targetTable = context.resolveTableName(table, reference.table());
        Set<String> targetColumns = new HashSet<>(context.getConfig().table(targetTable).getPkColumnNames());
        if (!targetColumns.equals(reference.columns().keySet())) {
            throw new ProcessorException("Composite reference [" + table + "." + column
                    + "] must map every primary-key column of table [" + targetTable + "]");
        }
        for (Map.Entry<String, String> entry : reference.columns().entrySet()) {
            result.put(entry.getValue(), columnProcessor.reference(table, targetTable, label, entry.getKey()));
        }
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
        List<String> sourcePks = context.getConfig().table(table).getPkColumnNames();
        if (sourcePks.size() != 1) {
            throw new ProcessorException("Many-to-many association [" + table + "." + column
                    + "] requires a single-column source primary key");
        }
        String sourcePk = sourcePks.get(0);
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
