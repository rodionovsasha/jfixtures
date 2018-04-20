package com.github.vkorobkov.jfixtures.config;

import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.YmlUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader {
    public Root load(String filePath) {
        Path path = Paths.get(filePath);
        return new Root(
            Node.root(YmlUtil.load(path))
        );
    }

    public Root defaultConfig() {
        return new Root(Node.emptyRoot());
    }
}
