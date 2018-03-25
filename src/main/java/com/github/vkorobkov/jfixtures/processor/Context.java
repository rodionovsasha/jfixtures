package com.github.vkorobkov.jfixtures.processor;

import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.domain.Table;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import lombok.Getter;

import java.util.*;

@Getter
class Context {
    private final List<Instruction> instructions = new ArrayList<>();
    private final RowsIndex rowsIndex = new RowsIndex();
    private final Set<String> completedTables = new HashSet<>();
    private final CircularPreventer circularPreventer = new CircularPreventer();
    private final Map<String, Table> tables;
    private final Root config;

    Context(Map<String, Table> tables, Root config) {
        this.tables = Collections.unmodifiableMap(tables);
        this.config = config;
    }
}
