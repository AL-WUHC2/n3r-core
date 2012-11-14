package org.n3r.core.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;
import org.n3r.core.lang.RBaseBean;

import com.ailk.xmlbean.JaxbBean;
import com.ailk.xmlbean.JaxbInnerBean;

import static org.junit.Assert.*;

public class RJaxbTest {

    @Test
    public void test0() {
        assertNotNull(new RJaxb());
    }

    @Test
    public void test1() throws Exception {
        Person person = new Person();
        person.setIdCode("id1");
        person.setName("aaa");
        User user = new User();
        user.setPerson(person);

        String xmlStr = RJaxb.bean2Xml(user);

        User user2 = RJaxb.xml2Bean(xmlStr, User.class);

        assertEquals(user, user2);
    }

    @Test
    public void test2() throws Exception {
        JaxbInnerBean inner = new JaxbInnerBean();
        inner.setAttrStr("str");
        inner.setAttrInt(123);
        JaxbBean jaxb = new JaxbBean();
        jaxb.setInner(inner);

        String xmlStr = RJaxb.bean2Xml(jaxb);

        JaxbBean jaxb2 = RJaxb.xml2Bean(xmlStr, JaxbBean.class);

        assertEquals(jaxb, jaxb2);
    }

    @XmlRootElement
    public static class User extends RBaseBean {
        private Person person;

        public void setPerson(Person person) {
            this.person = person;
        }

        public Person getPerson() {
            return person;
        }
    }

    public static class Person extends RBaseBean {
        private String idCode;
        private String name;

        public void setIdCode(String idCode) {
            this.idCode = idCode;
        }

        public String getIdCode() {
            return idCode;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
