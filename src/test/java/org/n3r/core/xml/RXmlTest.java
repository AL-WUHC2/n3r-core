package org.n3r.core.xml;

import java.util.Arrays;

import org.junit.Test;
import org.n3r.core.lang.RBaseBean;
import org.n3r.core.xml.annotation.RXCData;
import org.n3r.core.xml.annotation.RXRootElement;
import org.n3r.core.xml.bean.ListVoid;
import org.n3r.core.xml.bean.Person;
import org.n3r.core.xml.bean.User;
import org.n3r.core.xml.bean.UserAnno;

import static org.junit.Assert.*;

public class RXmlTest {

    @Test
    public void test0() {
        assertNotNull(new RXml());
    }

    @Test
    public void test1() {
        String xmlExpect = "<User><Friend><Age>13</Age><Name>bbb</Name></Friend><Friend><Age>14</Age><Name>ccc</Name></Friend><PersonInfo><Age>12</Age><Name>aaa</Name></PersonInfo></User>";

        Person person = new Person();
        person.setName("aaa");
        person.setAge(12);

        Person f1 = new Person();
        f1.setName("bbb");
        f1.setAge(13);

        Person f2 = new Person();
        f2.setName("ccc");
        f2.setAge(14);

        User user = new User();
        user.setPersonInfo(person);
        user.setFriends(Arrays.asList(f1, f2));

        String xml = RXml.beanToXml(user);
        assertEquals(xmlExpect, xml);

        User user2 = RXml.xmlToBean(xml, User.class);
        assertEquals(user, user2);
    }

    @Test
    public void test2() {
        String xml = "<Home><Family><Age>13</Age><Name>bbb</Name></Family><Family><Age>14</Age><Name>ccc</Name></Family><PersonInfo><Age>12</Age><Name>aaa</Name></PersonInfo></Home>";

        Person person = new Person();
        person.setName("aaa");
        person.setAge(12);

        Person f1 = new Person();
        f1.setName("bbb");
        f1.setAge(13);

        Person f2 = new Person();
        f2.setName("ccc");
        f2.setAge(14);

        UserAnno user = new UserAnno();
        user.setPersonInfo(person);
        user.setFriends(Arrays.asList(f1, f2));
        String actXml = RXml.beanToXml(user);
        assertEquals(xml, actXml);

        UserAnno user2 = RXml.xmlToBean(xml, UserAnno.class);
        assertEquals(user, user2);
    }

    @Test
    public void test3() {
        ListVoid bean = new ListVoid();
        bean.setItems(Arrays.asList("AAA", "BBB"));

        String xml = "<ListVoid><Item _type_=\"java.lang.String\">AAA</Item><Item _type_=\"java.lang.String\">BBB</Item></ListVoid>";
        assertEquals(xml, RXml.beanToXml(bean));

        ListVoid xmlToBean = RXml.xmlToBean(xml, ListVoid.class);
        assertEquals(bean, xmlToBean);
    }

    @Test
    public void testEmptyList() {
        String xmlExpect = "<User><PersonInfo><Age>12</Age><Name>aaa</Name></PersonInfo></User>";

        Person person = new Person();
        person.setName("aaa");
        person.setAge(12);

        User user = new User();
        user.setPersonInfo(person);
        user.setFriends(null);

        String xml = RXml.beanToXml(user);
        assertEquals(xmlExpect, xml);

        User user2 = RXml.xmlToBean(xml, User.class);
        assertEquals(user, user2);
    }

    @Test
    public void test4() {
        Device dev = new Device(1001, "Cables <Connectors", "CC1084", "038123-45627");

        String xml = "<device><DeviceId>1001</DeviceId><DeviceName>Cables &lt;Connectors</DeviceName>" +
                    "<ProductId>CC1084</ProductId><SerialNo>038123-45627</SerialNo></device>";
        assertEquals(xml, RXml.beanToXml(dev, "device"));

        String xml2 = "<Device><DeviceId>1001</DeviceId><DeviceName>Cables &lt;Connectors</DeviceName>" +
                    "<ProductId>CC1084</ProductId><SerialNo>038123-45627</SerialNo></Device>";
        assertEquals(xml2, RXml.beanToXml(dev));
    }

    public static class Device {
        public Device(long deviceId, String deviceName, String productId, String serialNo) {
            this.deviceId = deviceId;
            this.deviceName = deviceName;
            this.productId = productId;
            this.serialNo = serialNo;
        }

        private long deviceId;
        private String deviceName;
        private String productId;
        private String serialNo;

        /**
         * @return the deviceId
         */
        public long getDeviceId() {
            return deviceId;
        }

        /**
         * @param deviceId
         *            the deviceId to set
         */
        public void setDeviceId(long deviceId) {
            this.deviceId = deviceId;
        }

        /**
         * @return the deviceName
         */
        public String getDeviceName() {
            return deviceName;
        }

        /**
         * @param deviceName
         *            the deviceName to set
         */
        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        /**
         * @return the productId
         */
        public String getProductId() {
            return productId;
        }

        /**
         * @param productId
         *            the productId to set
         */
        public void setProductId(String productId) {
            this.productId = productId;
        }

        /**
         * @return the serialNo
         */
        public String getSerialNo() {
            return serialNo;
        }

        /**
         * @param serialNo
         *            the serialNo to set
         */
        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }
    }

    @RXRootElement("Root")
    public static class CdataBean extends RBaseBean {
        private String before;
        @RXCData
        private Cdata content;
        private String last;

        public void setBefore(String before) {
            this.before = before;
        }

        public String getBefore() {
            return before;
        }

        public void setContent(Cdata content) {
            this.content = content;
        }

        public Cdata getContent() {
            return content;
        }

        public void setLast(String last) {
            this.last = last;
        }

        public String getLast() {
            return last;
        }
    }

    @RXRootElement("Root")
    public static class Cdata extends RBaseBean {
        private String content;

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    @Test
    public void testCDATA() {
        String xml = "<Root><Before>Before</Before><Content><![CDATA[<Root><Content>Hello</Content></Root>]]></Content><Last>Last</Last></Root>";

        Cdata c = new Cdata();
        c.setContent("Hello");
        CdataBean bean = new CdataBean();
        bean.setBefore("Before");
        bean.setContent(c);
        bean.setLast("Last");
        String result = RXml.beanToXml(bean);
        assertEquals(xml, result);

        CdataBean bean2 = RXml.xmlToBean(xml, CdataBean.class);
        assertEquals(bean, bean2);
    }

}
