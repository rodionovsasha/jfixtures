package com.github.vkorobkov.jfixtures

import spock.lang.Specification

class JFixturesTest extends Specification {
    def "#ofConfig instantiates object with config path stored"() {
        expect:
        JFixtures.ofConfig("path/.conf").config == Optional.of("path/.conf")
    }

    def "#noConfig instantiates object with empty config"() {
        expect:
        JFixtures.noConfig().config == Optional.empty()
    }
}
