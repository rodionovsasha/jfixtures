package com.github.vkorobkov.jfixtures.util;

import lombok.val;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class StringUtil {
    private StringUtil() {
    }

    public static String removePrefixes(String s, String... prefixes) {
        for (String prefix : prefixes) {
            if (s.startsWith(prefix)) {
                return s.substring(prefix.length());
            }
        }
        return s;
    }

    public static Path cutOffExtension(Path path) {
        String name = path.getFileName().toString();
        val dotPisition = name.lastIndexOf('.');
        if (dotPisition == -1) {
            return path;
        }

        name = name.substring(0, dotPisition);
        val location = path.getParent();
        return location != null ? location.resolve(name) : Paths.get(name);
    }
}
