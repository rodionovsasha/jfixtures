package com.github.vkorobkov.jfixtures.util;

import lombok.val;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

public final class YmlUtil {
    private static final String YML_EXT = ".yml";
    private static final String YAML_EXT = ".yaml";

    private YmlUtil() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> load(Path file) throws IOException {
        Object loaded = new Yaml().load(Files.newInputStream(file));
        return loaded == null ? Collections.emptyMap() : (Map<String, Object>)loaded;
    }

    public static boolean hasTwin(Path filePath) {
        val count = Stream.of(YML_EXT, YAML_EXT)
                .map(ext -> cutOffExtension(filePath.toString()) + ext)
                .filter(name -> new File(name).exists())
                .count();

        if (count == 0) {
            throw new IllegalArgumentException("Neither " + YAML_EXT + " nor " + YML_EXT + " file extension: "
             + filePath);
        }

        return count == 2;
    }

    private static String cutOffExtension(String name) {
        val lastIndex = name.lastIndexOf(".");
        return lastIndex == -1 ? name : name.substring(0, lastIndex);
    }
}
