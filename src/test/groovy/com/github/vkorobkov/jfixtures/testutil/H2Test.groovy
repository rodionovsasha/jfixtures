package com.github.vkorobkov.jfixtures.testutil

import com.github.vkorobkov.jfixtures.JFixtures
import com.github.vkorobkov.jfixtures.sql.DataSourceUtil
import groovy.sql.Sql

trait H2Test extends YamlVirtualFolder {
    Sql getSql() { DataSourceUtil.sql }

    def executeFixtures(folderPath) {
        sql.execute(JFixtures.sql99(folderPath as String).asString())
    }

    def recreateTable(tableName, tableColumns) {
        sql.execute("DROP TABLE IF EXISTS " + tableName)
        sql.execute("CREATE TABLE " + tableName + " (" + tableColumns + ")")
    }
}