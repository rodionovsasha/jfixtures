package com.github.vkorobkov.jfixtures.fluent;

public interface JFixturesResult {
    String asString();

    void toFile(String name);
}
