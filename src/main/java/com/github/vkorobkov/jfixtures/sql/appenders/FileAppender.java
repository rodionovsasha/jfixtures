package com.github.vkorobkov.jfixtures.sql.appenders;

import com.github.vkorobkov.jfixtures.sql.Appender;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileAppender implements Appender, AutoCloseable {
    private final BufferedWriter writer;

    public FileAppender(String name) throws IOException {
        Path path = Paths.get(name);
        Files.deleteIfExists(path);
        this.writer = Files.newBufferedWriter(path);
    }

    @Override
    public void append(CharSequence sequence) throws IOException {
        writer.append(sequence);
    }

    @Override
    public void close() throws Exception {
        writer.close();
    }
}
