package com.github.vkorobkov.jfixtures.config.structure.util;


import com.github.vkorobkov.jfixtures.util.CollectionUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@FunctionalInterface
public interface TableMatcher {

    default boolean tableMatches(String table) {
        return flatTablesToMatch().anyMatch(pattern -> {
            if (pattern.startsWith("/")) {
                return Pattern.compile(pattern.substring(1)).matcher(table).matches();
            } else {
                return pattern.equals(table);
            }
        });
    }

    default Stream<String> flatTablesToMatch() {
        Set<String> patterns = new HashSet<>();
        CollectionUtil.flattenRecursively(tablesToMatch(), new SplitStringConsumer(patterns::add));
        return patterns.stream();
    }

    Object tablesToMatch();
}
