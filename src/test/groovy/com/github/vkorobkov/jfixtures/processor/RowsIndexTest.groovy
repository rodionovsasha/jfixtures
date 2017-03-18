package com.github.vkorobkov.jfixtures.processor

import com.github.vkorobkov.jfixtures.instructions.InsertRow
import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class RowsIndexTest extends Specification {

    RowsIndex index

    void setup() {
        index = new RowsIndex()
    }

    def "RowKey has valid equals and hashcode"() {
        expect:
        EqualsVerifier.forClass(RowsIndex.RowKey).verify()
    }

    def "reads insert instruction by table name and row key"() {
        given:
        def row = new InsertRow("users", "vlad", [:])

        when:
        index.visit(row)

        then:
        index.read("users", "vlad").get() == row
    }

    def "returns empty optional if instruction was not found"() {
        expect:
        !index.read("users", "vlad").present
    }
}
