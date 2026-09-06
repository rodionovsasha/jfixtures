package com.github.rodionovsasha.jfixtures.sql

import org.apache.commons.dbcp2.BasicDataSource

class DataSourceUtil {
    // Match the case-insensitive identifier lookup used by the original H2 1.x tests.
    private static final DATASOURCE_URL = "jdbc:h2:mem:jfixturesDb;DB_CLOSE_DELAY=-1;" +
        "DATABASE_TO_UPPER=false;CASE_INSENSITIVE_IDENTIFIERS=true"

    @Lazy
    static def sql = new groovy.sql.Sql(new BasicDataSource(url: DATASOURCE_URL))
}