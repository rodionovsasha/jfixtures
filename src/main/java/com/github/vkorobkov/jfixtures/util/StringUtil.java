package com.github.vkorobkov.jfixtures.util;

public final class StringUtil {
    private StringUtil() {
    }

    public static String removePrefixes(String s, String ... prefixes) {
        for (String prefix: prefixes) {
            if (s.startsWith(prefix)) {
                return s.substring(prefix.length());
            }
        }
        return s;
    }
}
