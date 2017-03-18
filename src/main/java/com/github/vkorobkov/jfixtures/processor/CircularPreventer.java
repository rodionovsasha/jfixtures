package com.github.vkorobkov.jfixtures.processor;

import java.util.ArrayDeque;
import java.util.Deque;

class CircularPreventer {
    private final static String ARROW = "-->";
    private final Deque<String> stack = new ArrayDeque<>();

    void doInStack(String element, Callback callback) {
        if (stack.contains(element)) {
            String chain = String.join(ARROW, stack);
            String message = "Circular dependency between tables found: " + chain + ARROW + element;
            throw new ProcessorException(message);
        }

        stack.addLast(element);
        callback.doInStack();
        stack.removeLast();
    }

    @FunctionalInterface
    public interface Callback {
        void doInStack();
    }
}
