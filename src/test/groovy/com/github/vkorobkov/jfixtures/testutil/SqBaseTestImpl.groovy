package com.github.vkorobkov.jfixtures.testutil

import com.github.vkorobkov.jfixtures.loader.FixtureValue
import com.github.vkorobkov.jfixtures.sql.SqlBase

class SqBaseTestImpl implements SqlBase {
    @Override
    String escapeTableOrColumnPart(String part) {
        "[$part]"
    }

    @Override
    String escapeValue(FixtureValue value) {
        "|$value|"
    }
}
