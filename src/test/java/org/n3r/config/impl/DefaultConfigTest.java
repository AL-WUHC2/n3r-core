package org.n3r.config.impl;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.n3r.config.Configable;

import static org.junit.Assert.*;

public class DefaultConfigTest {

    @Test
    public void test1() throws IOException {
        String propertyFile = "   a.b.c =  123  \r\n   b.c   :  word   \r\n  a.d   hello   ";
        Properties properties = new Properties();
        properties.load(IOUtils.toInputStream(propertyFile));

        Configable config = new DefaultConfigable(properties);
        assertTrue(config.exists("a.b.c"));
        assertFalse(config.exists("a.b.no"));

        Configable subset = config.subset("a");
        assertEquals("hello", subset.getStr("d"));
        subset = config.subset("a.");
        assertEquals("hello", subset.getStr("d"));
        config = config.subset("");

        assertEquals(0, config.getProperties().size());
    }

}
