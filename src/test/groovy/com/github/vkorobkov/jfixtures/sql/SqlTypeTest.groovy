package com.github.vkorobkov.jfixtures.sql

import com.github.vkorobkov.jfixtures.sql.dialects.MicrosoftSql
import com.github.vkorobkov.jfixtures.sql.dialects.MySql
import com.github.vkorobkov.jfixtures.sql.dialects.Sql99
import spock.lang.Specification

class SqlTypeTest extends Specification {
    def "SqlType positive cases"(String value, expected) {
        expect:
        SqlType.valueOf(value) == expected

        where:
        value           | expected
        "MYSQL"         | SqlType.MYSQL
        "MICROSOFT_SQL" | SqlType.MICROSOFT_SQL
        "SQL99"         | SqlType.SQL99
    }

    def "SqlType throws exception when wrong value is provided"() {
        when:
        SqlType.valueOf("wrong_type")

        then:
        thrown(IllegalArgumentException)
    }

    def "SqlType get sql dialects"() {
        expect:
        SqlType.MYSQL.sqlDialect.class == MySql
        SqlType.MICROSOFT_SQL.sqlDialect.class == MicrosoftSql
        SqlType.SQL99.sqlDialect.class == Sql99
    }
}
