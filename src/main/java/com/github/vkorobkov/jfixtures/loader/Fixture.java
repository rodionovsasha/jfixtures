package com.github.vkorobkov.jfixtures.loader;

import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

public final class Fixture {
    public final String name;

    @Getter
    private final Collection<FixtureRow> rows;

    public Fixture(String name, Collection<FixtureRow> rows) {
        this.name = name;
        this.rows = Collections.unmodifiableCollection(rows);
    }
}
