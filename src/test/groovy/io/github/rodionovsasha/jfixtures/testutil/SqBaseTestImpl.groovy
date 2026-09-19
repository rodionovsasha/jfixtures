package io.github.rodionovsasha.jfixtures.testutil

import io.github.rodionovsasha.jfixtures.sql.SqlBase

class SqBaseTestImpl implements SqlBase {
    @Override
    String escapeTableOrColumnPart(String part) {
        "[$part]"
    }
}
