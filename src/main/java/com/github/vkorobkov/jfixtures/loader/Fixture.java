package com.github.vkorobkov.jfixtures.loader;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

public final class Fixture {
    public final String name;

    private Collection<FixtureRow> rows;

    private final Supplier<Collection<FixtureRow>> rowsLoader;

    public Fixture(String name, Supplier<Collection<FixtureRow>> rowsLoader) {
        this.name = name;
        this.rowsLoader = rowsLoader;
    }

    public Collection<FixtureRow> getRows() {
        if (rows == null) {
            rows = Collections.unmodifiableCollection(rowsLoader.get());
        }
        return rows;
    }
}
