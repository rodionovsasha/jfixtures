package com.github.vkorobkov.jfixtures.testutil

import org.yaml.snakeyaml.Yaml

import java.nio.file.Files
import java.nio.file.Path

trait YamlVirtualDirectory implements WithTempFile, WithTestResource {
    Path unpackYamlToTempDirectory(String fileName) {
        def yamlPath = testResourcePath(fileName)
        def directoryPath = newTempSubDirectory(fileName)
        def yaml = new Yaml()

        def writeContent = { Path path, content ->
            switch(content) {
                case Map:
                    path.write(yaml.dumpAsMap(content))
                    break
                case String:
                    path.write(content)
                    break
            }
        }

        Closure nodeHandler
        nodeHandler = { String name, content, Path rootPath ->
            def isDirectory = name.startsWith("/")
            name = isDirectory ? name.substring(1) : name
            def path = rootPath.resolve(name)
            if (isDirectory) {
                Files.createDirectory(path)
                if (content instanceof Map<String, ?>) {
                    content.each { String subName, subContent ->
                        nodeHandler(subName, subContent, path)
                    }
                }
            }
            else {
                Files.createFile(path)
                writeContent(path, content)
            }
        }

        yaml.load(yamlPath.newReader()).each { String name, content ->
            nodeHandler(name, content, directoryPath)
        }

        directoryPath
    }
}
