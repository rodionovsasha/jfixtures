package com.github.vkorobkov.jfixtures.fluent.impl;

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
import java.util.List;

@NoArgsConstructor
@XmlRootElement(name = "instructions")
public class XmlJFixturesResultImpl extends JFixturesResultBase {
    public XmlJFixturesResultImpl(final String fixturesDirectory) {
        super(fixturesDirectory);
    }

    @XmlElements({
            @XmlElement(name = "instruction", type = CleanTable.class),
            @XmlElement(name = "instruction", type = CustomSql.class),
            @XmlElement(name = "instruction", type = InsertRow.class)
    })
    @Override
    List<Instruction> getInstructions() {
        return super.getInstructions();
    }

    @SneakyThrows
    @Override
    public String asString() {
        val writer = new StringWriter();
        getMarshaller().marshal(this, writer);

        return writer.toString();
    }

    @SneakyThrows
    @Override
    public void toFile(final String name) {
        getMarshaller().marshal(this, new File(name));
    }

    @SneakyThrows
    private Marshaller getMarshaller() {
        val jaxbContext = JAXBContext.newInstance(this.getClass());
        val marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }
}
