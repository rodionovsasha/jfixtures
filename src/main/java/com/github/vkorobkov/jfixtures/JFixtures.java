package com.github.vkorobkov.jfixtures;

import com.github.vkorobkov.jfixtures.config.ConfigLoader;
import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.domain.Table;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import com.github.vkorobkov.jfixtures.loader.DirectoryLoader;
import com.github.vkorobkov.jfixtures.loader.MapDataLoader;
import com.github.vkorobkov.jfixtures.processor.Processor;
import com.github.vkorobkov.jfixtures.result.Result;
import com.github.vkorobkov.jfixtures.util.CollectionUtil;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

@Getter
public final class JFixtures {
    public static final String DEFAULT_PROFILE = "default";

    private final Optional<String> config;
    private final Collection<Table> tables;
    private final String profile;

    private JFixtures(Optional<String> config, String profile) {
        this(config, profile, Collections.emptyList());
    }

    private JFixtures(Optional<String> config, String profile, Collection<Table> tables) {
        this.config = config;
        this.profile = profile;
        this.tables = Collections.unmodifiableCollection(tables);
    }

    public static JFixtures withConfig(Path config) {
        return withConfig(config.toString());
    }

    public static JFixtures withConfig(File config) {
        return withConfig(config.getAbsolutePath());
    }

    public static JFixtures withConfig(String config) {
        return new JFixtures(Optional.of(config), DEFAULT_PROFILE);
    }

    public static JFixtures noConfig() {
        return new JFixtures(Optional.empty(), DEFAULT_PROFILE);
    }

    public JFixtures loadDirectory(File path) {
        return loadDirectory(path.getAbsolutePath());
    }

    public JFixtures loadDirectory(Path path) {
        return loadDirectory(path.toString());
    }

    public JFixtures loadDirectory(String path) {
        return addTables(
            new DirectoryLoader(path).load()
        );
    }

    public JFixtures addTables(Table... tables) {
        return addTables(Arrays.asList(tables));
    }

    public JFixtures addTables(Map<String, Map<String, Object>> tables) {
        return addTables(MapDataLoader.loadTables(tables));
    }

    public JFixtures addTables(Collection<Table> newTables) {
        return new JFixtures(
            this.config,
            this.profile,
            CollectionUtil.concat(this.tables, newTables)
        );
    }

    public JFixtures withProfile(String profile) {
        return new JFixtures(this.config, profile, this.tables);
    }

    public Result compile() {
        List<Instruction> instructions = new Processor(Table.mergeTables(tables), loadConfig()).process();
        return new Result(instructions);
    }

    private Root loadConfig() {
        return config.map(this::loadConfig).orElseGet(Root::empty);
    }

    private Root loadConfig(String path) {
        return new ConfigLoader().load(path, profile);
    }
}
