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

    public static Path cutOffExtension(Path filePath) {
        String fileName = filePath.toFile().getName();
        val lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1) {
            return filePath;
        }

        fileName = fileName.substring(0, lastIndex);
        val parent = filePath.getParent();
        return parent != null ? parent.resolve(fileName) : Paths.get(fileName);
    }
}
