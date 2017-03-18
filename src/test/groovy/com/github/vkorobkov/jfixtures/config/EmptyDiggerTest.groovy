package com.github.vkorobkov.jfixtures.config

import spock.lang.Specification

class EmptyDiggerTest extends Specification {

    ConfigDigger digger

    void setup() {
        digger = new EmptyDigger()
    }

    def "always returns empty optional"(name) {
        expect:
        !digger.digValue(name).present

        where:
        name << ["refs", "refs:users", "anything"]
    }
}
