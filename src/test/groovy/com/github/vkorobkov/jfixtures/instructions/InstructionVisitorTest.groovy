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

    def "visiting CustomSql is doing nothing"() {
        expect:
        visitor.visit(new CustomSql("users", "BEGIN TRANSACTION;"))
    }


    static class TestVisitor implements InstructionVisitor {
        @Override
        void visit(InsertRow insertRow) {
        }
    }
}
