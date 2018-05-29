package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

import java.nio.file.Path

class ConfigLoaderTest extends Specification implements YamlVirtualDirectory {
    Path tmlDirectoryPath
    String path

    void setup() {
        tmlDirectoryPath = unpackYamlToTempDirectory("default.yml")
        path = "$tmlDirectoryPath/default/.conf.yml"
    }

    void cleanup() {
        tmlDirectoryPath.toFile().deleteDir()
    }

    def "::load loads config from file without profile"() {
        when:
        def config = new ConfigLoader().load(path, "default")

        then:
        config.referredTable("users", "avatar_id") == Optional.of("images")
        config.referredTable("content.comment", "ticket_id") == Optional.of("tickets")
    }

    def "::load loads config from file with profile"() {
        when:
        def config = new ConfigLoader().load(path, "unit")

        then:
        config.referredTable("users", "avatar_id") == Optional.of("unit_images")
    }

    def "::load loads config from file with profile which inherits another profile"() {
        when:
        def config = new ConfigLoader().load(path, "integration")

        then:
        config.referredTable("users", "avatar_id") == Optional.of("unit_images")
    }
}
