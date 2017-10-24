package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualFolder
import spock.lang.Specification

class ConfigLoaderTest extends Specification implements YamlVirtualFolder {
    def "dummy constructor test"() {
        expect:
        new ConfigLoader()
    }

    def "loads config normally"() {
        setup:
        def tmlFolderPath = unpackYamlToTempFolder("default.yml")
        def path = "$tmlFolderPath/default"
        def config =  ConfigLoader.load(path)

        expect:
        config.referredTable("users", "avatar_id") == Optional.of("images")
        config.referredTable("content.comment", "ticket_id") == Optional.of("tickets")

        cleanup:
        tmlFolderPath.toFile().deleteDir()
    }

    def "loads empty config if file not found"() {
        setup:
        def config = ConfigLoader.load(notExistingPath() as String)

        expect:
        config.referredTable("users", "avatar_id") == Optional.empty()
    }

    def "#load throws when found twins"() {
        setup:
        def tmlFolderPath = unpackYamlToTempFolder("twins.yml")
        def path = "$tmlFolderPath/default"

        when:
        ConfigLoader.load(path)

        then:
        def e = thrown(ConfigLoaderException)
        e.message.contains("Fixture's config exists with both extensions(yaml/yml)")

        cleanup:
        tmlFolderPath.toFile().deleteDir()
    }
}
