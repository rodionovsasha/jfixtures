package io.github.rodionovsasha.jfixtures.processor;

import io.github.rodionovsasha.jfixtures.instructions.InsertRow;
import io.github.rodionovsasha.jfixtures.instructions.InstructionVisitor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class RowsIndex implements InstructionVisitor {
    private final Map<RowKey, InsertRow> index = new HashMap<>();

    public Optional<InsertRow> read(String table, String rowName) {
        var key = new RowKey(table, rowName);
        var row = index.get(key);
        return Optional.ofNullable(row);
    }

    @Override
    public void visit(InsertRow row) {
        var key = new RowKey(row.getTable(), row.getRowName());
        index.put(key, row);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    static final class RowKey {
        private final String table;
        private final String rowName;
    }
}
