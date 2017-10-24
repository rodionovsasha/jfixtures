package com.github.vkorobkov.jfixtures.config;


import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public final class ConfigLoader {
    private static final String CONF_YML = ".conf.yml";
    private static final String CONF_YAML = ".conf.yaml";

    private ConfigLoader() {
    }

    public static Root load(String fixturesRoot) {
        return new Root(loadRootNode(fixturesRoot));
    }

    private static Node loadRootNode(String fixturesRoot) {
        Optional<Path> configPath = getConfigPath(fixturesRoot);

        if (configPath.isPresent()) {
            val path = configPath.get();
            if (YmlUtil.hasTwin(path)) {
                throw new ConfigLoaderException("Fixture's config exists with both extensions(yaml/yml).");
            }
            try {
                return Node.root(YmlUtil.load(path));
            } catch (IOException e) {
                return loadEmptyRoot();
            }
        } else {
            return loadEmptyRoot();
        }
    }

    private static Optional<Path> getConfigPath(String fixturesRoot) {
        return Stream
                .of(CONF_YAML, CONF_YML)
                .map(Paths.get(fixturesRoot)::resolve)
                .filter(Files::exists)
                .findFirst();
    }

    private static Node loadEmptyRoot() {
        log.warn("Neither file '" + CONF_YAML + "' nor '" + CONF_YML + "' not found, using defaults");
        return Node.emptyRoot();
    }
}
