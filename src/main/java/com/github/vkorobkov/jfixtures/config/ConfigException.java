package com.github.vkorobkov.jfixtures.config;

public class ConfigException extends RuntimeException {
    ConfigException(String message) {
        super(message);
    }

    ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}