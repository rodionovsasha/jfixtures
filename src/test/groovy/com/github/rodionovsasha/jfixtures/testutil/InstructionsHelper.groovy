package com.github.rodionovsasha.jfixtures.testutil

import com.github.rodionovsasha.jfixtures.config.structure.tables.CleanMethod
import com.github.rodionovsasha.jfixtures.domain.Value
import com.github.rodionovsasha.jfixtures.instructions.CleanTable
import com.github.rodionovsasha.jfixtures.instructions.InsertRow
import com.github.rodionovsasha.jfixtures.util.CollectionUtil

trait InstructionsHelper {

    def cleanTable(String table, CleanMethod method = CleanMethod.DELETE) {
        new CleanTable(table, method)
    }

    def insertRow(String table, String rowName, Map<String, ?> fields) {
        def values = CollectionUtil.mapValues(fields) { Value.of(it) }
        new InsertRow(table, rowName, values)
    }
}