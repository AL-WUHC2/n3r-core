package org.n3r.config;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    @Test
    public void test() {
        String str = Config.getStr("key");
        assertEquals("value", str);

        assertEquals(323222, Config.getInt("password"));
        assertEquals(33222, Config.getInt("user.pass"));
        
        assertEquals("this is my 323222", Config.getStr("password2"));
        assertEquals("abcabc", Config.getStr("word2"));
    }
}
