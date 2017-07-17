package com.github.vkorobkov.jfixtures.sql

import org.apache.commons.dbcp2.BasicDataSource

class DataSourceUtil {
    private static final DATASOURCE_URL = "jdbc:h2:mem:jfixturesDb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false"

    @Lazy
    static def sql = new groovy.sql.Sql(new BasicDataSource(url: DATASOURCE_URL))
}