package com.github.rodionovsasha.jfixtures.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

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
        var extPosition = name.lastIndexOf('.');
        if (extPosition == -1) {
            return path;
        }

        name = name.substring(0, extPosition);
        var location = path.getParent();
        return location != null ? path.getParent().resolve(name) : Paths.get(name);
    }
}
