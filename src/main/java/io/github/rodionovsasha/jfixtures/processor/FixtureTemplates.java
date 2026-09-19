package io.github.rodionovsasha.jfixtures.processor;

import io.github.rodionovsasha.jfixtures.config.structure.Root;
import io.github.rodionovsasha.jfixtures.domain.Row;
import io.github.rodionovsasha.jfixtures.domain.Table;
import io.github.rodionovsasha.jfixtures.domain.Value;
import io.github.rodionovsasha.jfixtures.domain.ValueType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Expands the deliberately small, opt-in fixture-template language. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class FixtureTemplates {
    private static final String DIRECTIVE = "$template";
    private static final int MAX_EXPANDED_ROWS = 10_000;
    private static final int RANGE_TO_GROUP = 3;
    private static final Pattern RANGE = Pattern.compile(
            "([A-Za-z_][A-Za-z0-9_]*)=(-?\\d+)\\.\\.(-?\\d+)"
    );
    private static final Pattern EXPRESSION = Pattern.compile("\\{\\{\\s*(.+?)\\s*}}", Pattern.DOTALL);

    static Collection<Table> expand(Collection<Table> tables, Root config) {
        if (!config.templatesEnabled()) {
            return tables;
        }
        List<Table> result = new ArrayList<>();
        for (Table table : tables) {
            result.add(expand(table));
        }
        return result;
    }

    private static Table expand(Table table) {
        Map<String, Row> rows = new LinkedHashMap<>();
        for (Row row : table.getRows()) {
            for (Row expanded : expand(row)) {
                if (rows.putIfAbsent(expanded.getName(), expanded) != null) {
                    throw new ProcessorException("Template expansion creates duplicate row ["
                            + table.getName() + "." + expanded.getName() + "]");
                }
            }
        }
        return Table.of(table.getName(), rows.values());
    }

    private static Collection<Row> expand(Row row) {
        Value directive = row.getColumns().get(DIRECTIVE);
        if (directive == null) {
            return List.of(row);
        }
        if (!(directive.getValue() instanceof String specification)) {
            throw new ProcessorException("Template directive [" + row.getName() + "." + DIRECTIVE
                    + "] must be a range such as [number=1..3]");
        }
        List<Range> ranges = parseRanges(row.getName(), specification);
        List<Row> result = new ArrayList<>();
        expandRow(row, ranges, 0, new LinkedHashMap<>(), result);
        return result;
    }

    private static List<Range> parseRanges(String row, String specification) {
        List<Range> result = new ArrayList<>();
        for (String source : specification.split(",", -1)) {
            Matcher matcher = RANGE.matcher(source.trim());
            if (!matcher.matches()) {
                throw new ProcessorException("Invalid template directive [" + row + ".$template]: ["
                        + specification + "]");
            }
            String name = matcher.group(1);
            if (result.stream().anyMatch(range -> range.name().equals(name))) {
                throw new ProcessorException("Template directive [" + row + ".$template] repeats variable ["
                        + name + "]");
            }
            result.add(new Range(name, Long.parseLong(matcher.group(2)),
                    Long.parseLong(matcher.group(RANGE_TO_GROUP))));
        }
        return result;
    }

    private static void expandRow(
            Row source,
            List<Range> ranges,
            int rangeIndex,
            Map<String, Long> variables,
            Collection<Row> result
    ) {
        if (rangeIndex == ranges.size()) {
            if (result.size() >= MAX_EXPANDED_ROWS) {
                throw new ProcessorException("Template expands to more than " + MAX_EXPANDED_ROWS + " rows");
            }
            result.add(render(source, variables));
            return;
        }
        Range range = ranges.get(rangeIndex);
        long step = range.from() <= range.to() ? 1 : -1;
        for (long value = range.from(); ; value += step) {
            variables.put(range.name(), value);
            expandRow(source, ranges, rangeIndex + 1, variables, result);
            if (value == range.to()) {
                break;
            }
        }
        variables.remove(range.name());
    }

    private static Row render(Row source, Map<String, Long> variables) {
        Map<String, Object> columns = new LinkedHashMap<>();
        source.getColumns().forEach((name, value) -> {
            if (!DIRECTIVE.equals(name)) {
                columns.put(name, render(value, variables));
            }
        });
        return Row.of(renderText(source.getName(), variables), columns);
    }

    private static Value render(Value value, Map<String, Long> variables) {
        if (!(value.getValue() instanceof String text)) {
            return value;
        }
        Matcher matcher = EXPRESSION.matcher(text);
        if (!matcher.find()) {
            return value;
        }
        matcher.reset();
        if (matcher.matches()) {
            long number = new Arithmetic(matcher.group(1), variables).evaluate();
            return value.getType() == ValueType.SQL ? Value.ofSql(String.valueOf(number)) : Value.of(number);
        }
        return value.getType() == ValueType.SQL
                ? Value.ofSql(renderText(text, variables))
                : Value.ofText(renderText(text, variables));
    }

    private static String renderText(String text, Map<String, Long> variables) {
        Matcher matcher = EXPRESSION.matcher(text);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, Matcher.quoteReplacement(
                    String.valueOf(new Arithmetic(matcher.group(1), variables).evaluate())
            ));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private record Range(String name, long from, long to) { }

    /** Parses integer arithmetic only: variables, literals, parentheses, and + - * / %. */
    private static final class Arithmetic {
        private final String expression;
        private final Map<String, Long> variables;
        private int position;

        private Arithmetic(String expression, Map<String, Long> variables) {
            this.expression = expression;
            this.variables = variables;
        }

        private long evaluate() {
            try {
                long result = expression();
                skipWhitespace();
                if (position != expression.length()) {
                    throw invalid();
                }
                return result;
            } catch (ArithmeticException cause) {
                throw invalid();
            }
        }

        private long expression() {
            long result = term();
            while (true) {
                skipWhitespace();
                if (consume('+')) {
                    result = Math.addExact(result, term());
                } else if (consume('-')) {
                    result = Math.subtractExact(result, term());
                } else {
                    return result;
                }
            }
        }

        private long term() {
            long result = factor();
            while (true) {
                skipWhitespace();
                if (consume('*')) {
                    result = Math.multiplyExact(result, factor());
                } else if (consume('/')) {
                    result /= factor();
                } else if (consume('%')) {
                    result %= factor();
                } else {
                    return result;
                }
            }
        }

        private long factor() {
            skipWhitespace();
            if (consume('(')) {
                long result = expression();
                skipWhitespace();
                if (!consume(')')) {
                    throw invalid();
                }
                return result;
            }
            if (consume('-')) {
                return Math.negateExact(factor());
            }
            if (position >= expression.length()) {
                throw invalid();
            }
            char first = expression.charAt(position);
            if (Character.isDigit(first)) {
                return number();
            }
            if (Character.isLetter(first) || first == '_') {
                return variable();
            }
            throw invalid();
        }

        private long number() {
            int start = position;
            while (position < expression.length() && Character.isDigit(expression.charAt(position))) {
                position++;
            }
            try {
                return Long.parseLong(expression.substring(start, position));
            } catch (NumberFormatException cause) {
                throw invalid();
            }
        }

        private long variable() {
            int start = position++;
            while (position < expression.length()) {
                char current = expression.charAt(position);
                if (!Character.isLetterOrDigit(current) && current != '_') {
                    break;
                }
                position++;
            }
            String name = expression.substring(start, position);
            Long value = variables.get(name);
            if (value == null) {
                throw new ProcessorException("Unknown template variable [" + name + "]");
            }
            return value;
        }

        private boolean consume(char expected) {
            if (position < expression.length() && expression.charAt(position) == expected) {
                position++;
                return true;
            }
            return false;
        }

        private void skipWhitespace() {
            while (position < expression.length() && Character.isWhitespace(expression.charAt(position))) {
                position++;
            }
        }

        private ProcessorException invalid() {
            return new ProcessorException("Invalid template expression [" + expression + "]");
        }
    }
}
