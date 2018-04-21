package com.github.vkorobkov.jfixtures.testutil

import com.github.vkorobkov.jfixtures.config.structure.tables.CleanMethod
import com.github.vkorobkov.jfixtures.domain.Value
import com.github.vkorobkov.jfixtures.instructions.CleanTable
import com.github.vkorobkov.jfixtures.instructions.InsertRow
import com.github.vkorobkov.jfixtures.util.CollectionUtil

trait InstructionsHelper {

    def cleanTable(String table, CleanMethod method = CleanMethod.DELETE) {
        new CleanTable(table, method)
    }

    def insertRow(String table, String rowName, Map<String, ?> fields) {
        def values = CollectionUtil.mapValues(fields) { Value.of(it) }
        new InsertRow(table, rowName, values)
    }
}