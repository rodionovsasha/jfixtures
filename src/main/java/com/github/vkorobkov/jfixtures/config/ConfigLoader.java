package com.github.vkorobkov.jfixtures.config;


import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ConfigLoader {
    private static final String CONF_YML = ".conf.yml";
    private static final String CONF_YAML = ".conf.yaml";
    private final Path path;

    public ConfigLoader(String fixturesRoot) {
        this.path = getConfigPath(fixturesRoot);
    }

    public Root load() {
        return new Root(loadRootNode());
    }

    private Node loadRootNode() {
        try {
            return Node.root(YmlUtil.load(path));
        } catch (IOException exception) {
            log.warn("file '" + CONF_YAML + "' or '" + CONF_YML + "' not found, using defaults");
            return Node.emptyRoot();
        }
    }

    private Path getConfigPath(String fixturesRoot) {
        val rootPath = Paths.get(fixturesRoot);
        val yamlPath = rootPath.resolve(CONF_YAML);
        val ymlPath = rootPath.resolve(CONF_YML);
        val yamlFileExists = yamlPath.toFile().exists();

        if (yamlFileExists && ymlPath.toFile().exists()) {
            throw new ConfigLoaderException("Fixture's config exists with both extensions.");
        }

        Path configPath = yamlPath;
        if (!yamlFileExists) {
            configPath = ymlPath;
        }
        return configPath;
    }
}
