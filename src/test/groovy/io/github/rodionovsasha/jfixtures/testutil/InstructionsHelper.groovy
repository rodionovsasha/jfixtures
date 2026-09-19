package io.github.rodionovsasha.jfixtures.testutil

import io.github.rodionovsasha.jfixtures.config.structure.tables.CleanMethod
import io.github.rodionovsasha.jfixtures.domain.Value
import io.github.rodionovsasha.jfixtures.instructions.CleanTable
import io.github.rodionovsasha.jfixtures.instructions.InsertRow
import io.github.rodionovsasha.jfixtures.util.CollectionUtil

trait InstructionsHelper {

    def cleanTable(String table, CleanMethod method = CleanMethod.DELETE) {
        new CleanTable(table, method)
    }

    def insertRow(String table, String rowName, Map<String, ?> fields) {
        def values = CollectionUtil.mapValues(fields) { Value.of(it) }
        new InsertRow(table, rowName, values)
    }
}