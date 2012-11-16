package org.n3r.core.xml;

import java.util.Arrays;

import org.junit.Test;
import org.n3r.core.xml.bean.ListVoid;
import org.n3r.core.xml.bean.Person;
import org.n3r.core.xml.bean.User;

import static org.junit.Assert.*;

public class RXmlTest {

    @Test
    public void test0() {
        assertNotNull(new RXml());
    }

    @Test
    public void test1() {
        String xmlExpect = "<User><PersonInfo><Name>aaa</Name><Age>12</Age></PersonInfo><Friend><Name>bbb</Name><Age>13</Age></Friend><Friend><Name>ccc</Name><Age>14</Age></Friend></User>";

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
        String xml = "<User><PersonInfo><Name>aaa</Name><Age>12</Age></PersonInfo><Family><Name>bbb</Name><Age>13</Age></Family><Family><Name>ccc</Name><Age>14</Age></Family></User>";

        User user2 = RXml.xmlToBean(xml, User.class);
        assertNull(user2.getFriends());
    }

    @Test
    public void test3() {
        ListVoid bean = new ListVoid();
        bean.setItems(Arrays.asList("AAA", "BBB"));

        String xml = RXml.beanToXml(bean);

        try {
            RXml.xmlToBean(xml, ListVoid.class);
            fail();
        }
        catch (RuntimeException e) {
            assertEquals("Unkown List Item Class for field items", e.getMessage());
        }
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

}
