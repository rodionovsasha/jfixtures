package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

class ConfigLoaderTest extends Specification implements YamlVirtualFolder {
    def "loads config normally"() {
        setup:
        def tmlFolderPath = unpackYamlToTempFolder("default.yml")
        def path = "$tmlFolderPath/default"
        def config = new ConfigLoader(path).load()

        expect:
        config.referredTable("users", "avatar_id") == Optional.of("images")
        config.referredTable("content.comment", "ticket_id") == Optional.of("tickets")
    }

    def "loads empty config if file not found"() {
        setup:
        def config = new ConfigLoader(notExistingPath() as String).load()

        expect:
        config.referredTable("users", "avatar_id") == Optional.empty()
    }
}
