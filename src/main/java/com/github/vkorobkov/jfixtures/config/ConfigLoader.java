package com.github.vkorobkov.jfixtures.config;

import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.YmlUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ConfigLoader {
    public Root load(String filePath) {
        Path path = Paths.get(filePath);
        return new Root(loadNode(path));
    }

    private Node loadNode(Path path) {
        if (Files.exists(path)) {
            return Node.root(YmlUtil.load(path));
        }
        log.info("File '" + path + "' not found, using default configuration");
        return Node.emptyRoot();
    }
}
