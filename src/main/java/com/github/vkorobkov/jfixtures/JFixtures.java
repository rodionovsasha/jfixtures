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
import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.Getter;
import lombok.val;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static JFixtures withConfig(Path config) {
        return withConfig(config.toString());
    }

    public static JFixtures withConfig(File config) {
        return withConfig(config.getAbsolutePath());
    }

    public static JFixtures withConfig(String config) {
        return new JFixtures(Optional.of(config));
    }

    public static JFixtures noConfig() {
        return new JFixtures(Optional.empty());
    }

    public JFixtures load(File path) {
        return load(path.getAbsolutePath());
    }

    public JFixtures load(Path path) {
        return load(path.toString());
    }

    public JFixtures load(String path) {
        Path dataPath = Paths.get(path);
        if (Files.isDirectory(dataPath)) {
            val data = new DirectoryLoader(path).load();
            return addTables(data);
        }
        val data = YmlUtil.load(dataPath);
        return addTables(data);
    }

    public JFixtures addTables(Table... tables) {
        return addTables(Arrays.asList(tables));
    }

    public JFixtures addTables(Map<String, ?> tables) {
        return addTables(MapDataLoader.loadTables(tables));
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
