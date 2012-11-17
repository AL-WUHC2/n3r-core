package org.n3r.core.xml.unmarshal;

import org.junit.Test;
import org.n3r.core.xml.RXml;
import org.n3r.core.xml.bean.AnnoBean;
import org.n3r.core.xml.bean.Person;
import org.n3r.core.xml.bean.PersonWithId;

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

    @Test
    public void test2() {
        String str = RXml.xmlToBean("<String>Hello</String>", String.class);
        assertEquals("Hello", str);
    }

    @Test
    public void test3() {
        PersonWithId person = new PersonWithId();
        person.setName("AAA");
        person.setAge(12);
        person.setIdCode("1234567890");

        PersonWithId person2 = RXml.xmlToBean(
                "<PersonWithId><Age>12</Age><IdCode>1234567890</IdCode><Name>AAA</Name></PersonWithId>",
                PersonWithId.class);
        assertEquals(person, person2);
    }

    @Test
    public void test4() {
        AnnoBean anno = new AnnoBean();
        anno.setParam1("HELLO");
        AnnoBean anno2 = RXml.xmlToBean("<root><branch>HELLO</branch></root>", AnnoBean.class);
        assertEquals(anno, anno2);
    }

}
