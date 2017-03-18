package com.github.vkorobkov.jfixtures.sql.appenders;

import com.github.vkorobkov.jfixtures.sql.Appender;

public class StringAppender implements Appender {
    private final StringBuilder sb = new StringBuilder();

    @Override
    public void append(CharSequence sequence) {
        sb.append(sequence);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
