package com.github.vkorobkov.jfixtures.config;

import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;


public class Config {
    private static final String CONF_YML = ".conf.yml";

    private static final String SECTION_REFS = "refs";

    @Getter
    private final YamlConfig yamlConf;

    public Config(String fixturesRoot) {
        Path path = Paths.get(fixturesRoot);
        this.yamlConf = loadFixturesConf(path.resolve(CONF_YML));
    }

    public Optional<String> referredTable(String table, String column) {
        return yamlConf.digValue(SECTION_REFS, table, column);
    }

    private YamlConfig loadFixturesConf(Path ymlConfPath) {
        try {
            return new YamlConfig(loadConfig(ymlConfPath));
        } catch (ConfigException ignored) {
            return new YamlConfig(Collections.emptyMap());
        }
    }

    private Map<String, Object> loadConfig(Path path) {
        try {
            return Collections.unmodifiableMap(YmlUtil.load(path));
        } catch (IOException exception) {
            String message = "Error loading configuration file: " + path;
            throw new ConfigException(message, exception);
        }
    }
}
