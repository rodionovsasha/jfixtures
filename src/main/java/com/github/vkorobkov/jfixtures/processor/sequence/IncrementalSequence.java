package com.github.vkorobkov.jfixtures.processor.sequence;

import com.github.vkorobkov.jfixtures.loader.FixtureValue;

import java.util.concurrent.atomic.AtomicLong;

public class IncrementalSequence implements Sequence {
    public static final long LOWER_BOUND = 10_000;
    private final AtomicLong value = new AtomicLong(LOWER_BOUND);

    @Override
    public FixtureValue next(String rowName) {
        return new FixtureValue(value.getAndIncrement());
    }
}
