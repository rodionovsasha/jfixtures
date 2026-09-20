package io.github.rodionovsasha.jfixtures.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Resolves file: references only in SQL hook configuration properties. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SqlFileReferences {
    private static final String PREFIX = "file:";

    static Map<String, Object> resolve(Map<String, Object> configuration, Path directory) {
        return map(configuration, directory);
    }

    private static Map<String, Object> map(Map<String, Object> source, Path directory) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(key, value(key, value, directory)));
        return result;
    }

    private static Object value(String key, Object value, Path directory) {
        if (value instanceof Map<?, ?> map) {
            return map(stringMap(map), directory);
        }
        if (value instanceof Collection<?> values) {
            return values.stream().map(item -> value(key, item, directory)).toList();
        }
        if (isSqlHook(key) && value instanceof String text && text.startsWith(PREFIX)) {
            return read(directory.resolve(text.substring(PREFIX.length())).normalize());
        }
        return value;
    }

    private static boolean isSqlHook(String key) {
        return "before_all".equals(key) || "after_all".equals(key)
                || "before_cleanup".equals(key) || "before_inserts".equals(key)
                || "after_inserts".equals(key);
    }

    private static Map<String, Object> stringMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(String.class.cast(key), value));
        return result;
    }

    @SneakyThrows
    private static String read(Path path) {
        return Files.readString(path).stripTrailing();
    }
}
