package com.github.vkorobkov.jfixtures.testutil

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

trait WithTempFile {
    Path newTempFile(String prefix = null, String suffix = null) {
        Files.createTempFile(getTmpDirectory(), prefix, suffix)
    }

    Path newTempSubDirectory(String name) {
        Files.createDirectory(getTmpDirectory().resolve(name))
    }

    Path getTmpDirectory() {
        def clazzName = getClass().canonicalName
        def path = Paths.get(System.getProperty("java.io.tmpdir")).resolve("jfixtures-$clazzName")
        if (!path.toFile().exists()) {
            Files.createDirectory(path)
            deleteDirectoryOnShutdown(path)
        }
        path
    }

    private deleteDirectoryOnShutdown(Path path) {
        Runtime.addShutdownHook {
            if (!path.toFile().deleteDir()) {
                System.err.println("Can not clean up directory $path")
            }
        }
    }
}
