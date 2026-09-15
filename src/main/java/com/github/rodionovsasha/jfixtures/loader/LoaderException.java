package com.github.rodionovsasha.jfixtures.loader;

public class LoaderException extends RuntimeException {
    LoaderException(String message) {
        super(message);
    }

    LoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
