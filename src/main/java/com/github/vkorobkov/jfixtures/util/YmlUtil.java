package com.github.vkorobkov.jfixtures.util;

import lombok.SneakyThrows;
import lombok.val;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.vkorobkov.jfixtures.util.StringUtil.cutOffExtension;

public final class YmlUtil {
    public static final String YML_EXT = ".yml";
    public static final String YAML_EXT = ".yaml";

    private YmlUtil() {
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public static Map<String, Object> load(Path file) {
        Object loaded = new Yaml().load(Files.newInputStream(file));
        return loaded == null ? Collections.emptyMap() : (Map<String, Object>)loaded;
    }

    public static boolean hasTwin(Path filePath) {
        val count = Stream.of(YML_EXT, YAML_EXT)
                .map(ext -> cutOffExtension(filePath) + ext)
                .map(Paths::get)
                .filter(Files::exists)
                .filter(path -> !Files.isDirectory(path))
                .count();

        if (count == 0) {
            throw new IllegalArgumentException("Neither " + filePath + " nor it's yaml/yml twin does exist");
        }

        return count == 2;
    }
}
