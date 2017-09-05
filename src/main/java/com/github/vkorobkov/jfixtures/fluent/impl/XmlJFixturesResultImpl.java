package com.github.vkorobkov.jfixtures.fluent.impl;

import lombok.SneakyThrows;
import lombok.val;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.StringWriter;

public class XmlJFixturesResultImpl extends JFixturesResultBase {
    public XmlJFixturesResultImpl(final String fixturesFolder) {
        super(fixturesFolder);
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
        val jaxbContext = JAXBContext.newInstance(JFixturesResultBase.class);
        val marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return marshaller;
    }
}
