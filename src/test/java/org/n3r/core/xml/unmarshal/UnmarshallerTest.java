package org.n3r.core.xml.unmarshal;

import org.junit.Test;
import org.n3r.core.xml.RXml;
import org.n3r.core.xml.bean.Person;

import static org.junit.Assert.*;

public class UnmarshallerTest {

    @Test
    public void test1() {
        Person person = new Person();
        person.setName("aaa");
        person.setAge(12);

        Person person2 = RXml.xmlToBean("<Person><Name>aaa</Name><Age>12</Age></Person>", Person.class);
        assertEquals(person, person2);

        String separtor = System.getProperty("line.separator");
        person2 = RXml.xmlToBean("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + separtor
                + "<Person>" + separtor
                + "    <Name>aaa</Name>" + separtor
                + "    <Age>12</Age>" + separtor
                + "</Person>" + separtor, Person.class);
        assertEquals(person, person2);
    }

}
