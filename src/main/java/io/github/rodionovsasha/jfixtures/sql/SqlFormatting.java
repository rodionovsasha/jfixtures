package io.github.rodionovsasha.jfixtures.sql;

/** Output-only formatting options for rendered SQL. */
public record SqlFormatting(String lineSeparator, int blankLinesBetweenStatements) {
    public SqlFormatting {
        if (lineSeparator == null || lineSeparator.isEmpty()) {
            throw new IllegalArgumentException("SQL line separator must not be empty");
        }
        if (blankLinesBetweenStatements < 0) {
            throw new IllegalArgumentException("SQL blank lines must not be negative");
        }
    }

    public static SqlFormatting compact() {
        return new SqlFormatting("\n", 0);
    }

    public String apply(String sql) {
        String separator = lineSeparator.repeat(blankLinesBetweenStatements + 1);
        return sql.replace("\n", separator);
    }
}
