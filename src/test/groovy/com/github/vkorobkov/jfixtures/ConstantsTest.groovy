package com.github.vkorobkov.jfixtures

import spock.lang.Specification

class ConstantsTest extends Specification {
    def "dummy constructor test"() {
        expect:
        new Constants()
    }
}
