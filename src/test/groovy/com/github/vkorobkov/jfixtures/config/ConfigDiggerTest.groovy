package com.github.vkorobkov.jfixtures.config

import spock.lang.Specification

class ConfigDiggerTest extends Specification {

    def "gets value by array of sections"() {
        expect:
        new TestConfigDigger().digValue("refs", "admin.users", "reg_id").get() == "refs:admin.users:reg_id"
    }

    private static class TestConfigDigger implements ConfigDigger {
        @Override
        <T> Optional<T> digValue(String name) {
            Optional.of(name) as Optional<T>
        }
    }
}
