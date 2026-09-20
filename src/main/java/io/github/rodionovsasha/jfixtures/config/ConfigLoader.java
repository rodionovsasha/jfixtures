package io.github.rodionovsasha.jfixtures.config;

import io.github.rodionovsasha.jfixtures.config.structure.Root;
import io.github.rodionovsasha.jfixtures.config.yaml.Node;
import io.github.rodionovsasha.jfixtures.util.YmlUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigLoader {
    public Root load(String filePath, String profile) {
        Path path = Paths.get(filePath);
        Map<String, Object> configuration = SqlFileReferences.resolve(
                YmlUtil.load(path),
                path.toAbsolutePath().getParent()
        );
        return Root.ofProfile(
                Node.root(configuration),
                profile
        );
    }
}
