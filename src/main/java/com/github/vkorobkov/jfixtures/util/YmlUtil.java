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

import static com.github.vkorobkov.jfixtures.util.StringUtil.cutOffExtension;

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
                .map(ext -> cutOffExtension(filePath) + ext)
                .filter(name -> {
                    val file = new File(name);
                    return file.exists() && !file.isDirectory();
                })
                .count();

        if (count == 0) {
            throw new IllegalArgumentException("Neither " + filePath + " nor it's yaml/yml twin does exist");
        }

        return count == 2;
    }
}
