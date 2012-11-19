package org.n3r.core.xml.marshal;

import org.junit.Test;
import org.n3r.core.xml.RXml;
import org.n3r.core.xml.bean.AnnoBean;
import org.n3r.core.xml.bean.Person;
import org.n3r.core.xml.bean.PersonWithId;
import org.n3r.core.xml.bean.UnnecessaryBean;

import static org.junit.Assert.*;

public class RMarshallerTest {

    @Test
    public void test1() {
        Person person = new Person();
        person.setName("aaa");
        person.setAge(12);

        String xml = RXml.beanToXml(person);
        assertEquals("<Person><Age>12</Age><Name>aaa</Name></Person>", xml);

        xml = RXml.beanToXml(person, "People");
        assertEquals("<People><Age>12</Age><Name>aaa</Name></People>", xml);

        String separtor = System.getProperty("line.separator");
        xml = RXml.beanToXmlWithFormat(person);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + separtor
                + "<Person>" + separtor
                + "    <Age>12</Age>" + separtor
                + "    <Name>aaa</Name>" + separtor
                + "</Person>" + separtor, xml);

        xml = RXml.beanToXmlWithFormat(person, "People");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + separtor
                + "<People>" + separtor
                + "    <Age>12</Age>" + separtor
                + "    <Name>aaa</Name>" + separtor
                + "</People>" + separtor, xml);
    }

    @Test
    public void test2() {
        String xml = RXml.beanToXml("Hello");
        assertEquals("<String>Hello</String>", xml);
    }

    @Test
    public void test3() {
        PersonWithId person = new PersonWithId();
        person.setName("AAA");
        person.setAge(12);
        person.setIdCode("1234567890");
        String xml = RXml.beanToXml(person);
        assertEquals("<PersonWithId><Age>12</Age><IdCode>1234567890</IdCode><Name>AAA</Name></PersonWithId>", xml);
    }

    @Test
    public void test4() {
        AnnoBean anno = new AnnoBean();
        anno.setParam1("HELLO");
        String xml = RXml.beanToXml(anno);
        assertEquals("<root><branch>HELLO</branch></root>", xml);
    }

    @Test
    public void testUnnecessary() {
        UnnecessaryBean bean = new UnnecessaryBean();
        bean.setName("aaa");
        String xml = RXml.beanToXml(bean);
        assertEquals("<Root><Name>aaa</Name></Root>", xml);
        bean.setNickName("bbb");
        xml = RXml.beanToXml(bean);
        assertEquals("<Root><Name>aaa</Name><NickName>bbb</NickName></Root>", xml);
    }
}
