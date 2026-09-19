package io.github.rodionovsasha.jfixtures;

import io.github.rodionovsasha.jfixtures.config.ConfigLoader;
import io.github.rodionovsasha.jfixtures.config.structure.Root;
import io.github.rodionovsasha.jfixtures.domain.Table;
import io.github.rodionovsasha.jfixtures.instructions.Instruction;
import io.github.rodionovsasha.jfixtures.loader.DirectoryLoader;
import io.github.rodionovsasha.jfixtures.loader.MapDataLoader;
import io.github.rodionovsasha.jfixtures.processor.Processor;
import io.github.rodionovsasha.jfixtures.result.Result;
import io.github.rodionovsasha.jfixtures.util.CollectionUtil;
import io.github.rodionovsasha.jfixtures.util.YmlUtil;
import lombok.Getter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public JFixtures load(File path) {
        return load(path.getAbsolutePath());
    }

    public JFixtures load(Path path) {
        return load(path.toString());
    }

    public JFixtures load(String path) {
        Path dataPath = Paths.get(path);
        if (Files.isDirectory(dataPath)) {
            var data = new DirectoryLoader(path).load();
            return addTables(data);
        }
        var data = YmlUtil.load(dataPath);
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
