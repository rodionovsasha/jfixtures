package com.github.vkorobkov.jfixtures.config

import com.github.vkorobkov.jfixtures.testutil.YamlVirtualDirectory
import spock.lang.Specification

class ConfigLoaderOldTest extends Specification implements YamlVirtualDirectory {
    def "dummy constructor test"() {
        expect:
        new ConfigLoaderOld()
    }

    def "loads config normally"() {
        setup:
        def tmlDirectoryPath = unpackYamlToTempDirectory("default.yml")
        def path = "$tmlDirectoryPath/default"
        def config =  ConfigLoaderOld.load(path)

        expect:
        config.referredTable("users", "avatar_id") == Optional.of("images")
        config.referredTable("content.comment", "ticket_id") == Optional.of("tickets")

        cleanup:
        tmlDirectoryPath.toFile().deleteDir()
    }

    def "loads empty config if file not found"() {
        setup:
        def config = ConfigLoaderOld.load(notExistingPath() as String)

        expect:
        config.referredTable("users", "avatar_id") == Optional.empty()
    }

    def "#load throws when found twins"() {
        setup:
        def tmlDirectoryPath = unpackYamlToTempDirectory("twins.yml")
        def path = "$tmlDirectoryPath/default"

        when:
        ConfigLoaderOld.load(path)

        then:
        def e = thrown(ConfigLoaderException)
        e.message.contains("Fixture's config exists with both extensions(yaml/yml)")

        cleanup:
        tmlDirectoryPath.toFile().deleteDir()
    }
}
