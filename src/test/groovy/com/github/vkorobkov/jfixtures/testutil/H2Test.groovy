package com.github.vkorobkov.jfixtures.testutil

import com.github.vkorobkov.jfixtures.JFixturesOld
import com.github.vkorobkov.jfixtures.sql.DataSourceUtil
import groovy.sql.Sql

trait H2Test extends YamlVirtualDirectory {
    Sql getSql() { DataSourceUtil.sql }

    def executeFixtures(directoryPath) {
        sql.execute(JFixturesOld.sql99(directoryPath as String).asString())
    }

    def recreateTable(tableName, tableColumns) {
        sql.execute("DROP TABLE IF EXISTS " + tableName)
        sql.execute("CREATE TABLE " + tableName + " (" + tableColumns + ")")
    }
}