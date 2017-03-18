package com.github.vkorobkov.jfixtures.processor.sequence;

import com.github.vkorobkov.jfixtures.loader.FixtureValue;

import java.util.HashMap;
import java.util.Map;

public class SequenceRegistry {
    private final Map<String, Sequence> sequences = new HashMap<>();

    public FixtureValue nextValue(String table, String rowName) {
        return sequences.get(table).next(rowName);
    }

    public void put(String table, Sequence sequence) {
        sequences.put(table, sequence);
    }
}
