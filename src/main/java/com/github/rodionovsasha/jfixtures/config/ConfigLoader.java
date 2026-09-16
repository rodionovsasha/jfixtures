package com.github.rodionovsasha.jfixtures.config;

import com.github.rodionovsasha.jfixtures.config.structure.Root;
import com.github.rodionovsasha.jfixtures.config.yaml.Node;
import com.github.rodionovsasha.jfixtures.util.YmlUtil;

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
