package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

import java.nio.file.Path

class ConfigTest extends Specification implements YamlVirtualFolder {
    Path tmlFolderPath
    Config config

    void setup() {
        tmlFolderPath = unpackYamlToTempFolder("default.yml")
        config = new Config(tmlFolderPath.toString())
    }

    void cleanup() {
        tmlFolderPath.toFile().deleteDir()
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
}
