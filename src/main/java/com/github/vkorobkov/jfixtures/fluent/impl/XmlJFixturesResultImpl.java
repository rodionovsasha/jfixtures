package com.github.vkorobkov.jfixtures.fluent.impl;

import com.github.vkorobkov.jfixtures.instructions.Instruction;
import lombok.SneakyThrows;
import lombok.val;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.StringWriter;
import java.util.List;

public class XmlJFixturesResultImpl extends JFixturesResultImpl {
    public XmlJFixturesResultImpl(final String fixturesFolder) {
        super(fixturesFolder);
    }

    @SneakyThrows
    @Override
    public String asString() {
        val writer = new StringWriter();
        getMarshaller(getInstructions()).marshal(this, writer);

        return writer.toString();
    }

    @SneakyThrows
    @Override
    public void toFile(final String name) {
        getMarshaller(getInstructions()).marshal(this, new File(name));
    }

    @SneakyThrows
    private Marshaller getMarshaller(final List<Instruction> instructions) {
        val jaxbContext = JAXBContext.newInstance(JFixturesResultImpl.class);
        val marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        this.setInstructions(instructions);
        return marshaller;
    }
}
