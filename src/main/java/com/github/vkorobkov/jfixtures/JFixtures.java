package com.github.vkorobkov.jfixtures;

import com.github.vkorobkov.jfixtures.config.ConfigLoader;
import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.domain.Table;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.DirectoryLoader;
import com.github.vkorobkov.jfixtures.processor.Processor;
import com.github.vkorobkov.jfixtures.result.Result;
import com.github.vkorobkov.jfixtures.util.CollectionUtil;
import lombok.Getter;

import java.util.*;

@Getter
public final class JFixtures {
    private final Optional<String> config;
    private final Collection<Table> tables;

    private JFixtures(Optional<String> config) {
        this(config, Collections.emptyList());
    }

    private JFixtures(Optional<String> config, Collection<Table> tables) {
        this.config = config;
        this.tables = Collections.unmodifiableCollection(tables);
    }

    public static JFixtures ofConfig(String config) {
        return new JFixtures(Optional.of(config));
    }

    public static JFixtures noConfig() {
        return new JFixtures(Optional.empty());
    }

    public JFixtures loadDirectory(String path) {
        return addTables(
            new DirectoryLoader(path).load()
        );
    }

    public JFixtures addTables(Table... tables) {
        return addTables(Arrays.asList(tables));
    }

    public JFixtures addTables(Collection<Table> newTables) {
        return new JFixtures(
            this.config,
            CollectionUtil.concat(this.tables, newTables)
        );
    }

    public Result compile() {
        List<Instruction> instructions = new Processor(Table.mergeTables(tables), loadConfig()).process();
        return new Result(instructions);
    }

    private Root loadConfig() {
        ConfigLoader loader = new ConfigLoader();
        return config.map(loader::load).orElseGet(loader::defaultConfig);
    }
}
