package com.github.vkorobkov.jfixtures.instructions;

import com.github.vkorobkov.jfixtures.loader.FixtureValue;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class InsertRow implements Instruction {
    private final String table;
    private final String rowName;
    private final Map<String, FixtureValue> values;

    public InsertRow(String table, String rowName, Map<String, FixtureValue> values) {
        this.table = table;
        this.rowName = rowName;
        this.values = Collections.unmodifiableMap(values);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
