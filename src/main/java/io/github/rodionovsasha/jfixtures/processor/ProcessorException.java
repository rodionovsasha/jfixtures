package io.github.rodionovsasha.jfixtures.processor;

public class ProcessorException extends RuntimeException {
    ProcessorException(String message) {
        super(message);
    }

    ProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
