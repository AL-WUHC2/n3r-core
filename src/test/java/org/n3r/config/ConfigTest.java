package org.n3r.config;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ConfigTest {

    @Test
    public void test() {
        assertEquals(323222, Config.getInt("password"));
        assertEquals(33222, Config.getInt("user.pass"));

        assertEquals("this is my 323222", Config.getStr("password2"));
        assertEquals("abcabc", Config.getStr("word2"));

        assertThat(Config.getStr("PageStaticBuilder.DEFAULT.httpSocketTimeoutSeconds"), is("60"));

        Configable subset = Config.subset("PageStaticBuilder.DEFAULT");
        assertThat(subset.getInt("httpSocketTimeoutSeconds"), is(60));

        Configable subset2 = subset.subset("addRsyncDir");
        System.out.println(subset2.getProperties());
    }
}
