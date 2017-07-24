package com.github.vkorobkov.jfixtures.config;

import java.util.Collections;

public class TablesConfig extends Config {
    private static final String SECTION_TABLES = "tables";
    private static final String SECTION_APPLIES_TO = "applies_to";
    private static final String SECTION_PRIMARY_KEY = "pk";
    private static final String SECTION_GENERATE = "generate";

    public TablesConfig(final YamlConfig yamlConfig) {
        super(yamlConfig);
    }

    public boolean shouldAutoGeneratePk(String tableName) {
        return getYamlConfig()
                .digNode(SECTION_TABLES).orElse(Collections.emptyMap())
                .keySet().stream()
                .map(section -> SECTION_TABLES + ":" + section)
                .filter(section -> tableMatches(section, tableName, SECTION_APPLIES_TO))
                .map(this::extractGenerateValue)
                .reduce((current, last) -> last).orElse(true);
    }

    private boolean extractGenerateValue(String section) {
        return getYamlConfig().digRequiredValue(section, SECTION_PRIMARY_KEY, SECTION_GENERATE);
    }
}
