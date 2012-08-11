package org.n3r.core.xml;

import static org.junit.Assert.*;

import org.junit.Test;

public class RXmlTest {
    @Test
    public void test() {
        Device dev = new Device(1001, "Cables <Connectors", "CC1084", "038123-45627");

        String xml = "<device><deviceId>1001</deviceId><deviceName>Cables &lt;Connectors</deviceName>" +
                "<productId>CC1084</productId><serialNo>038123-45627</serialNo></device>";
        assertEquals(xml, RXml.toXML(dev, "device"));

        String xml2 = "<Device><deviceId>1001</deviceId><deviceName>Cables &lt;Connectors</deviceName>" +
                "<productId>CC1084</productId><serialNo>038123-45627</serialNo></Device>";
        assertEquals(xml2, RXml.toXML(dev));
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
        public long getDeviceId() { return deviceId; }

        /**
         * @param deviceId
         *            the deviceId to set
         */
        public void setDeviceId(long deviceId) { this.deviceId = deviceId; }

        /**
         * @return the deviceName
         */
        public String getDeviceName() { return deviceName; }

        /**
         * @param deviceName
         *            the deviceName to set
         */
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

        /**
         * @return the productId
         */
        public String getProductId() { return productId; }

        /**
         * @param productId
         *            the productId to set
         */
        public void setProductId(String productId) { this.productId = productId; }

        /**
         * @return the serialNo
         */
        public String getSerialNo() { return serialNo; }

        /**
         * @param serialNo
         *            the serialNo to set
         */
        public void setSerialNo(String serialNo) { this.serialNo = serialNo;}
    }
}
