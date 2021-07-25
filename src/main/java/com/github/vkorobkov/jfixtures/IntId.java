package com.github.vkorobkov.jfixtures;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class IntId {
    public static final int LOWER_BOUND = 100_000;
    public static final int RANGE = Integer.MAX_VALUE - LOWER_BOUND;

    private IntId() {
    }

    public static List<Integer> many(String... aliases) {
        return Arrays.stream(aliases).map(IntId::one).collect(Collectors.toList());
    }

    /**
     * Calculates and returns a new Integer hash for provided String alias.
     * <p>
     * Very similar to plain @java.lang.String#hashCode() but the range is decreased:
     * - the lower bound is LOWER_BOUND, inclusively
     * - the higher bound is Integer.MAX_VALUE, inclusively
     */
    public static int one(String alias) {
        int hash = alias.hashCode() % RANGE;
        return LOWER_BOUND + Math.abs(hash);
    }
}
