package com.github.vkorobkov.jfixtures.processor.sequence;

import com.github.vkorobkov.jfixtures.loader.FixtureValue;

@FunctionalInterface
public interface Sequence {
    FixtureValue next(String rowName);
}
