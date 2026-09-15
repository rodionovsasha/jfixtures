package com.github.rodionovsasha.jfixtures.instructions;

import com.github.rodionovsasha.jfixtures.config.structure.tables.CleanMethod;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.xml.bind.annotation.XmlAttribute;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CleanTable implements Instruction {
    @XmlAttribute
    private static final String TYPE = "CleanTable";
    @XmlAttribute
    private String table;
    @XmlAttribute
    private CleanMethod cleanMethod;

    @Override
    public void accept(InstructionVisitor visitor) {
        visitor.visit(this);
    }
}
