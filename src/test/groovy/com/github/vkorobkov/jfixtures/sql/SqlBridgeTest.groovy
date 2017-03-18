package com.github.vkorobkov.jfixtures.sql

import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.sql.appenders.StringAppender
import com.github.vkorobkov.jfixtures.testutil.SqBaseTestImpl
import spock.lang.Specification

class SqlBridgeTest extends Specification {

    Sql sql
    SqlBridge bridge
    Appender appender

    void setup() {
        sql = Spy(SqBaseTestImpl)
        appender = new StringAppender()
        bridge = new SqlBridge(sql, appender)
    }

    def "bride calls sql passing appender and keeping the order"() {
        given:
        def instructions = [
            new CleanTable("users"),
            new InsertRow("users", "vlad", [:]),
            new InsertRow("users", "diman", [:]),
        ]

        when:
        bridge.apply(instructions)

        then:
        1 * sql.cleanTable(appender, instructions[0])

        then:
        1 * sql.insertRow(appender, instructions[1])

        then:
        1 * sql.insertRow(appender, instructions[2])
    }
}
