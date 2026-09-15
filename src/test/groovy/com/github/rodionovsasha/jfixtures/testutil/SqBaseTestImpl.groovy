package com.github.rodionovsasha.jfixtures.testutil

import com.github.rodionovsasha.jfixtures.sql.SqlBase

class SqBaseTestImpl implements SqlBase {
    @Override
    String escapeTableOrColumnPart(String part) {
        "[$part]"
    }
}
