package com.github.vkorobkov.jfixtures.testutil

import com.github.vkorobkov.jfixtures.sql.SqlBase

class SqBaseTestImpl implements SqlBase {
    @Override
    String escapeTableOrColumnPart(String part) {
        "[$part]"
    }
}
