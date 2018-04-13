package com.github.vkorobkov.jfixtures.testutil

import com.github.vkorobkov.jfixtures.RootTestPackageInfo

import java.nio.file.Path
import java.nio.file.Paths

trait WithTestResource {
    Path notExistingPath(suffix = "") {
        for(;;) {
            def rand = UUID.randomUUID() as String
            Path path = Paths.get(rand + suffix)
            if (!path.toFile().exists()) {
                return path
            }
        }
    }

    Path testResourcePath(fileName) {
        def className = deCapitilize(getClass().simpleName)
        def fullPath = resourcesDirectory() + "/" + className + "/" + fileName
        def uri = getClass().getResource(fullPath).toURI()
        Paths.get(uri)
    }

    private resourcesDirectory() {
        def pathSplitted =  relativeClassName().split("\\.")
        if (pathSplitted.size() == 1) {
            return ""
        }
        def directory = pathSplitted[0..-2].join("/")
        "/$directory"
    }

    private relativeClassName() {
        def packagePrefix = RootTestPackageInfo.package.name
        getClass().canonicalName.substring(packagePrefix.length() + 1)
    }

    private deCapitilize(String name) {
        name.replaceAll(/([A-Z])/, /_$1/).toLowerCase().replaceAll(/^_/, '')
    }
}
