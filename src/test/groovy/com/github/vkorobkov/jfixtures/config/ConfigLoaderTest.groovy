package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

class ConfigLoaderTest extends Specification implements YamlVirtualDirectory {
    def "#load loads config from file"() {
        setup:
        def tmlDirectoryPath = unpackYamlToTempDirectory("default.yml")
        def path = "$tmlDirectoryPath/default/.conf.yml"
        def config = new ConfigLoader().load(path)

        expect:
        config.referredTable("users", "avatar_id") == Optional.of("images")
        config.referredTable("content.comment", "ticket_id") == Optional.of("tickets")

        cleanup:
        tmlDirectoryPath.toFile().deleteDir()
    }

    def "#defaultConfig return default empty config"() {
        setup:
        def config = new ConfigLoader().defaultConfig()

        expect:
        config.referredTable("users", "avatar_id") == Optional.empty()
    }
}
