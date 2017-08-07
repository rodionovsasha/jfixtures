package com.github.vkorobkov.jfixtures.config.yaml;

public class NodeMissingException extends RuntimeException {
    public NodeMissingException(String node) {
        super("Node [" + node + "] is required but missing");
    }
}
