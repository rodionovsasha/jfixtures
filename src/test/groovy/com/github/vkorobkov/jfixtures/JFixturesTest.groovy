package com.github.vkorobkov.jfixtures

import com.github.vkorobkov.jfixtures.sql.SqlType
import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class JFixturesTest extends Specification implements YamlVirtualFolder {
    Path tmpFolderPath
    String outputPath

    def DEFAULT_EXPECTED_SQL = """DELETE FROM "users";
            |INSERT INTO "users" ("id", "name", "age") VALUES (1, 'Vlad', 29);
            |""".stripMargin()
    def MYSQL_EXPECTED_SQL = """DELETE FROM `users`;
            |INSERT INTO `users` (`id`, `name`, `age`) VALUES (1, 'Vlad', 29);
            |""".stripMargin()
    def MSSQL_EXPECTED_SQL = """DELETE FROM [users];
            |INSERT INTO [users] ([id], [name], [age]) VALUES (1, 'Vlad', 29);
            |""".stripMargin()

    void setup() {
        tmpFolderPath = unpackYamlToTempFolder("default.yml")
        outputPath = tmpFolderPath.resolve("out.sql") as String
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
    }

    def "dummy constructor"() {
        expect:
        new JFixtures()
    }

    def "mysql fixture to string"() {
        expect:
        JFixtures.mysql(tmpFolderPath as String).asString() == MYSQL_EXPECTED_SQL
    }

    def "mysql fixture to file"() {
        when:
        JFixtures.mysql(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == MYSQL_EXPECTED_SQL
    }

    def "by dialect fixture to a string"() {
        expect:
        JFixtures.byDialect(tmpFolderPath as String, SqlType.SQL99).asString() == DEFAULT_EXPECTED_SQL
    }

    def "by dialect fixture to a file"() {
        when:
        JFixtures.byDialect(tmpFolderPath as String, SqlType.SQL99).toFile(outputPath)

        then:
        new File(outputPath).text == DEFAULT_EXPECTED_SQL
    }

    def "MSSQL fixture to a string"() {
        expect:
        JFixtures.msSql(tmpFolderPath as String).asString() == MSSQL_EXPECTED_SQL
    }

    def "MSSQL fixture to a file"() {
        when:
        JFixtures.msSql(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == MSSQL_EXPECTED_SQL
    }

    def "SQLite fixture to a string"() {
        expect:
        JFixtures.sql99(tmpFolderPath as String).asString() == DEFAULT_EXPECTED_SQL
    }

    def "SQLite fixture to a file"() {
        when:
        JFixtures.sql99(tmpFolderPath as String).toFile(outputPath)

        then:
        new File(outputPath).text == DEFAULT_EXPECTED_SQL
    }
}
