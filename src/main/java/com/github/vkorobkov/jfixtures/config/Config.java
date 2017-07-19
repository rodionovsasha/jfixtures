package com.github.vkorobkov.jfixtures.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Config {
    private static final String CONF_YML = ".conf.yml";
    private static final String AUTO_GENERATE_PK = "autoGeneratePk";
    private final ConfigDigger fixturesConf;

    public Config(String fixturesRoot) {
        Path path = Paths.get(fixturesRoot);
        this.fixturesConf = loadFixturesConf(path.resolve(CONF_YML));
    }

    public Optional<String> referredTable(String table, String column) {
        return fixturesConf.digValue("refs", table, column);
    }

    public boolean shouldAutoGeneratePk(String tableName) {
        return fixturesConf.<Boolean>digValue(AUTO_GENERATE_PK, tableName).orElse(true);
    }

    private ConfigDigger loadFixturesConf(Path ymlConfPath) {
        try {
            return new YamlConfig(ymlConfPath);
        } catch (ConfigException ignored) {
            return new EmptyDigger();
        }
    }
}
