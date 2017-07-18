package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class ConfigTest extends Specification implements YamlVirtualFolder {
    Path tmpFolderPath
    Path tmpFolderPathWithPk
    Config config
    Config configWithPk

    void setup() {
        tmpFolderPath = unpackYamlToTempFolder("default.yml")
        tmpFolderPathWithPk = unpackYamlToTempFolder("auto_generate_pk.yml")
        config = new Config(tmpFolderPath.toString())
        configWithPk = new Config(tmpFolderPathWithPk.toString())
    }

    void cleanup() {
        tmpFolderPath.toFile().deleteDir()
        tmpFolderPathWithPk.toFile().deleteDir()
    }

    def "reads referred table"() {
        expect:
        config.referredTable("users", "avatar_id").get() == "images"
    }

    def "reads referred table with dot in the table name"() {
        expect:
        config.referredTable("content.comment", "ticket_id").get() == "tickets"
    }

    def "return empty optional if no referred table found"() {
        expect:
        !config.referredTable("aliens", "controlled_human").present
    }

    def "returns empty if no config file found"() {
        when:
        config = new Config(notExistingPath().toString())

        then:
        !config.referredTable("users", "avatar_id").present
    }

    def "should auto generate PK when autoGeneratePk flag does not exist"() {
        expect:
        config.shouldAutoGeneratePk("users")
    }

    def "should auto generate PK when autoGeneratePk flag has true value"() {
        expect:
        configWithPk.shouldAutoGeneratePk("comments")
        configWithPk.shouldAutoGeneratePk("teams")
    }

    def "should not auto generate PK when autoGeneratePk flag has false value"() {
        expect:
        !configWithPk.shouldAutoGeneratePk("users")
        !configWithPk.shouldAutoGeneratePk("friends")
    }
}
