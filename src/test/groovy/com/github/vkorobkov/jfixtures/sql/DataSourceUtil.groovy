package com.github.vkorobkov.jfixtures.sql

import org.apache.commons.dbcp2.BasicDataSource
import spock.lang.Shared

import javax.sql.DataSource

class DataSourceUtil {
    private static final String DATASOURCE_URL = "jdbc:h2:mem:jfixturesDb;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false"

    private static DataSource dataSource

    @Shared
    public static groovy.sql.Sql sql = new groovy.sql.Sql(getDataSource())

    private static DataSource getDataSource() {
        if (dataSource == null) {
            DataSource dataSource = new BasicDataSource()
            dataSource.setUrl(DATASOURCE_URL)
            this.dataSource = dataSource
        }
        return dataSource
    }
}