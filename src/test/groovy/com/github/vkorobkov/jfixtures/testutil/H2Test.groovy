package com.github.vkorobkov.jfixtures.testutil

import com.github.vkorobkov.jfixtures.JFixtures
import com.github.vkorobkov.jfixtures.sql.DataSourceUtil
import groovy.sql.Sql

trait H2Test extends YamlVirtualDirectory {
    Sql getSql() { DataSourceUtil.sql }

    def executeFixtures(directoryPath) {
        def fixtures = JFixtures
            .noConfig()
            .load(directoryPath)
            .compile()
            .toSql99()
            .toString()

        sql.execute(fixtures)
    }

    def executeFixtures(directoryPath, configPath) {
        def fixtures = JFixtures
            .withConfig(configPath)
            .load(directoryPath)
            .compile()
            .toSql99()
            .toString()

        sql.execute(fixtures)
    }

    def recreateTable(tableName, tableColumns) {
        sql.execute("DROP TABLE IF EXISTS " + tableName)
        sql.execute("CREATE TABLE " + tableName + " (" + tableColumns + ")")
    }
}