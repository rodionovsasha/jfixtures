package com.github.vkorobkov.jfixtures.loader;

import com.github.vkorobkov.jfixtures.util.StringUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@EqualsAndHashCode
@ToString
@Getter
public final class FixtureValue {
    public static final String PREFIX_SQL = "sql:";
    public static final String PREFIX_TEXT = "text:";

    private final Object value;
    @XmlAttribute
    private final ValueType type;

    public FixtureValue(Object value) {
        if (value instanceof String) {
            String str = (String)value;
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
        return value == null ? "NULL" : String.valueOf(value);
    }

    private ValueType determineType(String value) {
        return value.startsWith(PREFIX_SQL) ? ValueType.SQL : ValueType.TEXT;
    }
}
