package com.github.vkorobkov.jfixtures.instructions;

import com.github.vkorobkov.jfixtures.loader.FixtureValue;

import java.util.Collections;
import java.util.Map;

public class InsertRow implements Instruction {
    public final String table;
    public final String rowName;
    public final Map<String, FixtureValue> values;

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
