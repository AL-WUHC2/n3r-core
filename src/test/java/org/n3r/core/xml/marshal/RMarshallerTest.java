package org.n3r.core.xml.marshal;

import org.junit.Test;
import org.n3r.core.xml.RXml;
import org.n3r.core.xml.bean.Person;

import static org.junit.Assert.*;

public class RMarshallerTest {

    @Test
    public void test1() {
        Person person = new Person();
        person.setName("aaa");
        person.setAge(12);

        String xml = RXml.beanToXml(person);
        assertEquals("<Person><Name>aaa</Name><Age>12</Age></Person>", xml);

        xml = RXml.beanToXml(person, "People");
        assertEquals("<People><Name>aaa</Name><Age>12</Age></People>", xml);

        String separtor = System.getProperty("line.separator");
        xml = RXml.beanToXmlWithFormat(person);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + separtor
                + "<Person>" + separtor
                + "    <Name>aaa</Name>" + separtor
                + "    <Age>12</Age>" + separtor
                + "</Person>" + separtor, xml);

        xml = RXml.beanToXmlWithFormat(person, "People");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + separtor
                + "<People>" + separtor
                + "    <Name>aaa</Name>" + separtor
                + "    <Age>12</Age>" + separtor
                + "</People>" + separtor, xml);
    }

    @Test
    public void test2() {
        String xml = RXml.beanToXml("Hello");
        assertEquals("<String>Hello</String>", xml);
    }

}
