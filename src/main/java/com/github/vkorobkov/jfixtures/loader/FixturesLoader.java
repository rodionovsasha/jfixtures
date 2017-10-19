package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.config.structure.Root;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

public class FixturesLoader {
    private static final String YML_EXT = ".yml";
    private static final String YAML_EXT = ".yaml";
    private final String path;
    private final Root config;

    public FixturesLoader(String path, Root config) {
        this.path = path;
        this.config = config;
    }

    public Map<String, Fixture> load() {
        try {
            return Files
                    .walk(Paths.get(path))
                    .filter(this::isFile)
                    .filter(this::isYml)
                    .filter(this::isNotConfig)
                    .map(this::loadFixture)
                    .collect(Collectors.toMap(fixture -> fixture.name, fixture -> fixture));
        } catch (IOException cause) {
            String message = "Can not load fixtures from directory: " + path;
            throw new LoaderException(message, cause);
        }
    }

    private boolean isFile(Path file) {
        return !Files.isDirectory(file);
    }

    private boolean isYml(Path file) {
        val name = file.toFile().getName();
        return name.endsWith(YAML_EXT) || name.endsWith(YML_EXT);
    }

    private boolean isNotConfig(Path file) {
        return !file.toFile().getName().startsWith(".");
    }

    private Fixture loadFixture(Path file) {
        String name = getFixtureName(file);
        val baseColumns = config.columns().forTable(name);
        return new Fixture(name, new YmlRowsLoader(file, baseColumns));
    }

    private String getFixtureName(Path file) {
        String separator = file.getFileSystem().getSeparator();
        String relativePath = Paths.get(path).relativize(file).toString();

        if (relativePath.endsWith(YML_EXT)) {
            relativePath = relativePath.substring(0, relativePath.lastIndexOf(YML_EXT));
            checkDuplicateFixtureExists(path + separator + relativePath + YAML_EXT);
        }

        if (relativePath.endsWith(YAML_EXT)) {
            relativePath = relativePath.substring(0, relativePath.lastIndexOf(YAML_EXT));
            checkDuplicateFixtureExists(path + separator + relativePath + YML_EXT);
        }

        if (relativePath.contains(".")) {
            String message = "Do not use dots in file names. Use nested folders instead. Wrong fixture: " + file;
            throw new LoaderException(message);
        }

        return relativePath.replace(separator, ".");
    }

    private void checkDuplicateFixtureExists(String path) {
        if (new File(path).exists()) {
            throw new LoaderException("Fixture '" + path + "' exists with both extensions.");
        }
    }
}
