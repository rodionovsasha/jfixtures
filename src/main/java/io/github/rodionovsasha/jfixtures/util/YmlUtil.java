package io.github.rodionovsasha.jfixtures.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.rodionovsasha.jfixtures.util.StringUtil.cutOffExtension;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YmlUtil {
    public static final String YML_EXT = ".yml";
    public static final String YAML_EXT = ".yaml";

    @SneakyThrows
    public static Map<String, Object> load(Path file) {
        String document = Files.readString(file).replaceFirst("^\\s*!omap\\s*(?:\\R|$)", "");
        Object loaded = new Yaml(new SafeConstructor(new LoaderOptions())).load(document);
        return asMap(loaded);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object loaded) {
        if (loaded == null) {
            return Collections.emptyMap();
        }
        if (loaded instanceof Map<?, ?> map) {
            return stringKeyedMap(map);
        }
        if (loaded instanceof List<?> entries) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Object entry : entries) {
                if (!(entry instanceof Map<?, ?> map) || map.size() != 1) {
                    throw new IllegalArgumentException("YAML !omap entries must be one-entry maps");
                }
                result.putAll(stringKeyedMap(map));
            }
            return result;
        }
        throw new IllegalArgumentException("YAML fixture document must be a map or !omap");
    }

    private static Map<String, Object> stringKeyedMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(String.class.cast(key), value));
        return result;
    }

    public static boolean hasTwin(Path filePath) {
        var count = Stream.of(YML_EXT, YAML_EXT)
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
