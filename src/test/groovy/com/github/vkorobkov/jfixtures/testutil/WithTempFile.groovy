package com.github.vkorobkov.jfixtures.testutil

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

trait WithTempFile {
    Path newTempFile(String prefix = null, String suffix = null) {
        Files.createTempFile(getTmpFolder(), prefix, suffix)
    }

    Path newTempSubFolder(String name) {
        Files.createDirectory(getTmpFolder().resolve(name))
    }

    Path getTmpFolder() {
        def clazzName = getClass().canonicalName
        def path = Paths.get(System.getProperty("java.io.tmpdir")).resolve("jfixtures-$clazzName")
        if (!path.toFile().exists()) {
            Files.createDirectory(path)
            deleteFolderOnShutdown(path)
        }
        path
    }

    private deleteFolderOnShutdown(Path path) {
        Runtime.addShutdownHook {
            if (!path.toFile().deleteDir()) {
                System.err.println("Can not clean up folder $path")
            }
        }
    }
}
