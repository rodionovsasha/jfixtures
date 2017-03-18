package com.github.vkorobkov.jfixtures.instructions

import spock.lang.Specification

class InstructionVisitorTest extends Specification {
    TestVisitor visitor

    void setup() {
        visitor = new TestVisitor()
    }

    def "visiting CleanTable is doing nothing"() {
        expect:
        visitor.visit(new CleanTable("users"))
    }

    static class TestVisitor implements InstructionVisitor {
        @Override
        void visit(InsertRow insertRow) {
        }
    }
}
