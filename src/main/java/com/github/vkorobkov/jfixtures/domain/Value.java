package com.github.vkorobkov.jfixtures.domain;

import com.github.vkorobkov.jfixtures.Constants;
import com.github.vkorobkov.jfixtures.util.StringUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@EqualsAndHashCode
@ToString
@Getter
public final class Value {
    public static final String PREFIX_SQL = "sql:";
    public static final String PREFIX_TEXT = "text:";

    private final Object value;
    @XmlAttribute
    private final ValueType type;

    public static Value of(Object value) {
        return (value instanceof Value) ? (Value)value : new Value(value);
    }

    public static Value ofSql(String sql) {
        return Value.of(PREFIX_SQL + sql);
    }

    public static Value ofText(String text) {
        return Value.of(PREFIX_TEXT + text);
    }

    public static Value ofNull() {
        return Value.of(null);
    }

    private Value(Object value) {
        checkSupported(value);
        if (value instanceof String) {
            String str = (String) value;
            this.type = determineType(str);
            this.value = StringUtil.removePrefixes(str, PREFIX_SQL, PREFIX_TEXT);
        } else {
            this.value = value;
            this.type = ValueType.AUTO;
        }
    }

    @XmlValue
    String getXmlRepresentation() {
        return String.valueOf(value);
    }

    public String getSqlRepresentation() {
        return (value instanceof String) ? (String) value : String.valueOf(value).toUpperCase();
    }

    private ValueType determineType(String value) {
        return value.startsWith(PREFIX_SQL) ? ValueType.SQL : ValueType.TEXT;
    }

    private void checkSupported(Object value) {
        if (!isSupported(value)) {
            String message = "Type [" + value.getClass() + "] is not supported by JFixtures at the moment\n"
                    + "Read more on " + Constants.WIKI_TYPE_CONVERSIONS;
            throw new IllegalArgumentException(message);
        }
    }

    private boolean isSupported(Object value) {
        return value == null || value instanceof Number || value instanceof String || value instanceof Boolean;
    }
}
