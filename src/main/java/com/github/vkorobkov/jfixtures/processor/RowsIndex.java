package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.instructions.InstructionVisitor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.val;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class RowsIndex implements InstructionVisitor {
    private final Map<RowKey, InsertRow> index = new HashMap<>();

    public Optional<InsertRow> read(String table, String rowName) {
        val key = new RowKey(table, rowName);
        val row = index.get(key);
        return Optional.ofNullable(row);
    }

    @Override
    public void visit(InsertRow row) {
        val key = new RowKey(row.getTable(), row.getRowName());
        index.put(key, row);
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    static final class RowKey {
        public final String table;
        public final String rowName;
    }
}
