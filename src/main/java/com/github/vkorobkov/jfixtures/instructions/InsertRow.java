package com.github.vkorobkov.jfixtures.instructions;

import com.github.vkorobkov.jfixtures.loader.FixtureValue;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Map;

@NoArgsConstructor
@Getter
public class InsertRow implements Instruction {
    @XmlAttribute
    private static final String TYPE = "InsertRow";
    @XmlAttribute
    private String table;
    @XmlAttribute
    private String rowName;
    @XmlElement
    private Map<String, FixtureValue> values;

    public InsertRow(String table, String rowName, Map<String, FixtureValue> values) {
        this.table = table;
        this.rowName = rowName;
        this.values = Collections.unmodifiableMap(values);
    }

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
