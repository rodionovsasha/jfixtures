package com.github.vkorobkov.jfixtures;

public final class Id {
    public static final int LOWER_BOUND = 100_000;
    public static final int RANGE = Integer.MAX_VALUE - LOWER_BOUND;

    private Id() {
    }

    public static int one(String alias) {
        int hash = alias.hashCode() % RANGE;
        return LOWER_BOUND + Math.abs(hash);
    }
}
