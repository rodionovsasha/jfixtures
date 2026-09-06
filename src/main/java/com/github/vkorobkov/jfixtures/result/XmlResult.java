package com.github.rodionovsasha.jfixtures.result;

import com.github.rodionovsasha.jfixtures.instructions.CleanTable;
import com.github.rodionovsasha.jfixtures.instructions.CustomSql;
import com.github.rodionovsasha.jfixtures.instructions.InsertRow;
import com.github.rodionovsasha.jfixtures.instructions.Instruction;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.StringWriter;
import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;
import static jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

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
