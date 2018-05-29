package com.github.vkorobkov.jfixtures.result;

import com.github.vkorobkov.jfixtures.instructions.CleanTable;
import com.github.vkorobkov.jfixtures.instructions.CustomSql;
import com.github.vkorobkov.jfixtures.instructions.InsertRow;
import com.github.vkorobkov.jfixtures.instructions.Instruction;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.StringWriter;
import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

@NoArgsConstructor
@XmlRootElement(name = "instructions")
public class XmlResult implements StringResult {
    private Collection<Instruction> instructions;

    public XmlResult(Collection<Instruction> instructions) {
        this.instructions = unmodifiableCollection(instructions);
    }

    @XmlElements({
        @XmlElement(name = "instruction", type = CleanTable.class),
        @XmlElement(name = "instruction", type = CustomSql.class),
        @XmlElement(name = "instruction", type = InsertRow.class)
    })
    Collection<Instruction> getInstructions() {
        return instructions;
    }

    @SneakyThrows
    @Override
    public String toString() {
        val writer = new StringWriter();
        getMarshaller().marshal(this, writer);
        return writer.toString();
    }

    @SneakyThrows
    @Override
    public void toFile(String name) {
        getMarshaller().marshal(this, new File(name));
    }

    @SneakyThrows
    private Marshaller getMarshaller() {
        val jaxbContext = JAXBContext.newInstance(this.getClass());
        val marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }
}
