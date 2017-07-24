package com.github.vkorobkov.jfixtures.config;

import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Config {
    private static final String CONF_YML = ".conf.yml";
    private static final String SECTION_REFS = "refs";

    @Getter
    private final YamlConfig yamlConfig;

    public Config(String fixturesRoot) {
        Path path = Paths.get(fixturesRoot);
        this.yamlConfig = loadFixturesConf(path.resolve(CONF_YML));
    }

    public Config(final YamlConfig yamlConf) {
        this.yamlConfig = yamlConf;
    }

    public Optional<String> referredTable(String table, String column) {
        return yamlConfig.digValue(SECTION_REFS, table, column);
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

    boolean tableMatches(String section, String table, String sectionFlag) {
        Set<String> patterns = new HashSet<>();
        visitValuesRecursively(yamlConfig.digRequiredValue(section, sectionFlag), patterns::add);

        return patterns.stream().anyMatch(pattern -> {
            if (pattern.startsWith("/")) {
                if (Pattern.compile(pattern.substring(1)).matcher(table).matches()) {
                    return true;
                }
            } else if (pattern.equals(table)) {
                return true;
            }
            return false;
        });
    }

    void visitValuesRecursively(Object section, Consumer<String> consumer) {
        yamlConfig.visitValuesRecursively(section, item -> Arrays
                .asList((String.valueOf(item)).split(","))
                .forEach(s -> consumer.accept(s.trim()))
        );
    }
}
