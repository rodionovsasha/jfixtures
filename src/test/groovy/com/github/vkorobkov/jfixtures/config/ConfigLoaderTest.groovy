package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

class ConfigLoaderTest extends Specification implements YamlVirtualFolder {
    def "loads yml config normally"() {
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

    def "loads yaml config normally"() {
        setup:
        def tmlFolderPath = unpackYamlToTempFolder("config.yaml")
        def path = "$tmlFolderPath/default"
        def config = new ConfigLoader(path).load()

        expect:
        config.referredTable("users", "avatar_id") == Optional.of("images")
        config.referredTable("content.comment", "ticket_id") == Optional.of("tickets")
    }

    def "getConfigPath throws when exists with both extensions"() {
        setup:
        def tmlFolderPath = unpackYamlToTempFolder("both_configs.yaml")
        def path = "$tmlFolderPath/default"

        when:
        new ConfigLoader(path).load()

        then:
        def e = thrown(ConfigLoaderException)
        e.message == "Fixture's config exists with both extensions."
    }
}
