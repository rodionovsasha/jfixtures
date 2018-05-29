package com.github.vkorobkov.jfixtures.config;

import com.github.vkorobkov.jfixtures.config.structure.Root;
import com.github.vkorobkov.jfixtures.config.yaml.Node;
import com.github.vkorobkov.jfixtures.util.YmlUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigLoader {
    public Root load(String filePath, String profile) {
        Path path = Paths.get(filePath);
        return Root.ofProfile(
                Node.root(YmlUtil.load(path)),
                profile
        );
    }
}
