package org.n3r.eson;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class MapTest {

    @Test
    public void test_null() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(null, "123");
        assertEquals("{null:123}", new Eson().toString(map));

        Map<Object, Object> map2 = new Eson().parse("{1:\"2\",\"3\":4,'5':6}", Map.class);
        assertEquals("2", map2.get(1));
        assertEquals(4, map2.get("3"));
        assertEquals(6, map2.get("5"));
    }
}
