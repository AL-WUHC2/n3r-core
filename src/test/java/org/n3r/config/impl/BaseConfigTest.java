package org.n3r.config.impl;

import java.util.Properties;

import org.junit.Test;
import org.n3r.config.Configable;
import org.n3r.config.ex.ConfigNotFoundException;
import org.n3r.config.ex.ConfigValueFormatException;

import static org.junit.Assert.*;

public class BaseConfigTest {
    public static class MyBaseConfig extends BaseConfigable {
        private String configValue;
        private boolean existsKey;

        public MyBaseConfig(String configValue, boolean existsKey) {
            this.configValue = configValue;
            this.existsKey = existsKey;
        }

        public boolean exists(String key) {
            return existsKey;
        }

        public Properties getProperties() {
            return null;
        }

        public String getStr(String key) {
            return configValue;
        }

        public <T> T get(String prefix) {
            return null;
        }

        public Configable subset(String prefix) {
            return null;
        }

    }

    @Test
    public void getStr() {
        MyBaseConfig myBaseConfig = new MyBaseConfig("value", false);

        assertEquals("value", myBaseConfig.getStr("key", "defaultValue"));
    }

    @Test
    public void getInt() {
        MyBaseConfig myBaseConfig = new MyBaseConfig("value", false);

        try {
            myBaseConfig.getInt("key");
            fail();
        }
        catch (ConfigNotFoundException ex) {
            assertEquals("key not found in config system", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig("123value", true);
        assertEquals(123, myBaseConfig.getInt("key"));

        try {
            myBaseConfig = new MyBaseConfig("value", true);
            myBaseConfig.getInt("key");
            fail();
        }
        catch (ConfigValueFormatException ex) {
            assertEquals("key's value [value] is not an int", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig(".123value", true);
        assertEquals(0, myBaseConfig.getInt("key"));

        myBaseConfig = new MyBaseConfig("-0.123value", true);
        assertEquals(0, myBaseConfig.getInt("key"));

        myBaseConfig = new MyBaseConfig("-1.123value", true);
        assertEquals(-1, myBaseConfig.getInt("key"));
    }

    @Test
    public void getLong() {
        MyBaseConfig myBaseConfig = new MyBaseConfig("value", false);

        try {
            myBaseConfig.getLong("key");
            fail();
        }
        catch (ConfigNotFoundException ex) {
            assertEquals("key not found in config system", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig("123value", true);
        assertEquals(123L, myBaseConfig.getLong("key"));

        try {
            myBaseConfig = new MyBaseConfig("value", true);
            myBaseConfig.getLong("key");
            fail();
        }
        catch (ConfigValueFormatException ex) {
            assertEquals("key's value [value] is not a long", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig(".123value", true);
        assertEquals(0L, myBaseConfig.getLong("key"));

        myBaseConfig = new MyBaseConfig("-0.123value", true);
        assertEquals(0L, myBaseConfig.getLong("key"));

        myBaseConfig = new MyBaseConfig("-1.123value", true);
        assertEquals(-1L, myBaseConfig.getLong("key"));
    }

    @Test
    public void getBool() {
        MyBaseConfig myBaseConfig = new MyBaseConfig("value", false);

        try {
            myBaseConfig.getBool("key");
            fail();
        }
        catch (ConfigNotFoundException ex) {
            assertEquals("key not found in config system", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig("true", true);
        assertTrue(myBaseConfig.getBool("key"));

        myBaseConfig = new MyBaseConfig("yes", true);
        assertTrue(myBaseConfig.getBool("key"));

        myBaseConfig = new MyBaseConfig("y", true);
        assertTrue(myBaseConfig.getBool("key"));

        myBaseConfig = new MyBaseConfig("on", true);
        assertTrue(myBaseConfig.getBool("key"));

        myBaseConfig = new MyBaseConfig("false", true);
        assertFalse(myBaseConfig.getBool("key"));

        myBaseConfig = new MyBaseConfig("other", true);
        assertFalse(myBaseConfig.getBool("key"));
    }

    @Test
    public void getFloat() {
        MyBaseConfig myBaseConfig = new MyBaseConfig("value", false);

        try {
            myBaseConfig.getFloat("key");
            fail();
        }
        catch (ConfigNotFoundException ex) {
            assertEquals("key not found in config system", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig("123.124value", true);
        assertEquals(123.124, myBaseConfig.getFloat("key"), 0.0001);

        try {
            myBaseConfig = new MyBaseConfig("value", true);
            myBaseConfig.getFloat("key");
            fail();
        }
        catch (ConfigValueFormatException ex) {
            assertEquals("key's value [value] is not a float", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig(".123value", true);
        assertEquals(.123, myBaseConfig.getFloat("key"), 0.0001);

        myBaseConfig = new MyBaseConfig("-0.123value", true);
        assertEquals(-0.123, myBaseConfig.getFloat("key"), 0.0001);

        myBaseConfig = new MyBaseConfig("-1.123value", true);
        assertEquals(-1.123, myBaseConfig.getFloat("key"), 0.0001);
    }

    @Test
    public void getDouble() {
        MyBaseConfig myBaseConfig = new MyBaseConfig("value", false);

        try {
            myBaseConfig.getDouble("key");
            fail();
        }
        catch (ConfigNotFoundException ex) {
            assertEquals("key not found in config system", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig("123.124value", true);
        assertEquals(123.124, myBaseConfig.getDouble("key"), 0.0001);

        try {
            myBaseConfig = new MyBaseConfig("value", true);
            myBaseConfig.getDouble("key");
            fail();
        }
        catch (ConfigValueFormatException ex) {
            assertEquals("key's value [value] is not a double", ex.getMessage());
        }

        myBaseConfig = new MyBaseConfig(".123value", true);
        assertEquals(.123, myBaseConfig.getDouble("key"), 0.0001);

        myBaseConfig = new MyBaseConfig("-0.123value", true);
        assertEquals(-0.123, myBaseConfig.getDouble("key"), 0.0001);

        myBaseConfig = new MyBaseConfig("-1.123value", true);
        assertEquals(-1.123, myBaseConfig.getDouble("key"), 0.0001);
    }

    @Test
    public void getIntDefault() {
        MyBaseConfig myBaseConfig = new MyBaseConfig(null, false);
        assertEquals(123, myBaseConfig.getInt("key", 123));

        myBaseConfig = new MyBaseConfig("value", true);
        assertEquals(234, myBaseConfig.getInt("key", 234));

        myBaseConfig = new MyBaseConfig("123value", true);
        assertEquals(123, myBaseConfig.getInt("key", 234));

        myBaseConfig = new MyBaseConfig(".123value", true);
        assertEquals(234, myBaseConfig.getInt("key", 234));
    }

    @Test
    public void getLongDefault() {
        MyBaseConfig myBaseConfig = new MyBaseConfig(null, false);
        assertEquals(123L, myBaseConfig.getLong("key", 123L));

        myBaseConfig = new MyBaseConfig("value", true);
        assertEquals(234L, myBaseConfig.getLong("key", 234L));

        myBaseConfig = new MyBaseConfig("123value", true);
        assertEquals(123L, myBaseConfig.getLong("key", 234L));

        myBaseConfig = new MyBaseConfig(".123value", true);
        assertEquals(234L, myBaseConfig.getLong("key", 234L));
    }

    @Test
    public void getBoolDefault() {
        MyBaseConfig myBaseConfig = new MyBaseConfig(null, false);
        assertTrue(myBaseConfig.getBool("key", true));

        myBaseConfig = new MyBaseConfig("value", true);
        assertFalse(myBaseConfig.getBool("key", true));
    }

    @Test
    public void getFloatDefault() {
        MyBaseConfig myBaseConfig = new MyBaseConfig(null, false);
        assertEquals(12.3f, myBaseConfig.getFloat("key", 12.3f), 0.01);

        myBaseConfig = new MyBaseConfig("value", true);
        assertEquals(23.4f, myBaseConfig.getFloat("key", 23.4f), 0.01);

        myBaseConfig = new MyBaseConfig("123value", true);
        assertEquals(123f, myBaseConfig.getFloat("key", 23.4f), 0.01);

        myBaseConfig = new MyBaseConfig(".123value", true);
        assertEquals(.123f, myBaseConfig.getFloat("key", 23.4f), 0.0001);
    }

    @Test
    public void getDoubleDefault() {
        MyBaseConfig myBaseConfig = new MyBaseConfig(null, false);
        assertEquals(12.3, myBaseConfig.getDouble("key", 12.3), 0.01);

        myBaseConfig = new MyBaseConfig("value", true);
        assertEquals(23.4, myBaseConfig.getDouble("key", 23.4), 0.01);

        myBaseConfig = new MyBaseConfig("123value", true);
        assertEquals(123, myBaseConfig.getDouble("key", 23.4), 0.01);

        myBaseConfig = new MyBaseConfig(".123value", true);
        assertEquals(.123, myBaseConfig.getDouble("key", 23.4), 0.0001);
    }
}
