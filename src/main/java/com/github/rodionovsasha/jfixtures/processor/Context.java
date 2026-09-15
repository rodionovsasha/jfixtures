
package com.github.rodionovsasha.jfixtures.processor;

import com.github.rodionovsasha.jfixtures.config.structure.Root;
import com.github.rodionovsasha.jfixtures.domain.Table;
import com.github.rodionovsasha.jfixtures.instructions.Instruction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
class Context {
    private final List<Instruction> instructions = new ArrayList<>();
    private final RowsIndex rowsIndex = new RowsIndex();
    private final Set<String> completedTables = new HashSet<>();
    private final CircularPreventer circularPreventer = new CircularPreventer();
    private final Map<String, Table> tables;
    private final Root config;

    Context(Collection<Table> tables, Root config) {
        this.tables = tables.stream().collect(Collectors.toMap(Table::getName, fixture -> fixture));
        this.config = config;
    }
}
