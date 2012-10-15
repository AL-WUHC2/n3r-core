package org.n3r.eson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayTest {
    @Test
    public void testByteArray() throws UnsupportedEncodingException {
        HashMap<String, byte[]> hashMap = new HashMap<String, byte[]>();
        byte[] bytes = "黄老邪1".getBytes("UTF-8");
        hashMap.put("name", bytes);
        hashMap.put("bingoo", "".getBytes());

        String string = new Eson().toString(hashMap);
        assertEquals("{bingoo:\"\",name:6buE6ICB6YKqMQ==}", string);
        assertEquals("{\"bingoo\":\"\",\"name\":\"6buE6ICB6YKqMQ==\"}", new Eson().std().toString(hashMap));
        assertEquals("{bingoo:'',name:'6buE6ICB6YKqMQ=='}", new Eson().valueQuote('\'').toString(hashMap));

        Map<String, Object> map = new Eson().parse(string, Map.class);
        assertEquals("", map.get("bingoo"));
        assertEquals("6buE6ICB6YKqMQ==", map.get("name"));

        ByteArrayBean bean = new ByteArrayBean();
        assertEquals("{ba:null}", new Eson().toString(bean));
        bean = new Eson().parse("{ba:null}", ByteArrayBean.class);
        assertNull(bean.getBa());

        bean.setBa(bytes);
        assertEquals("{ba:6buE6ICB6YKqMQ==}", new Eson().toString(bean));

        ByteArrayBean bean2 = new Eson().parse("{ba:6buE6ICB6YKqMQ==}", ByteArrayBean.class);
        assertArrayEquals(bean.getBa(), bean2.getBa());
    }

    public static class ByteArrayBean {
        private byte[] ba;

        public byte[] getBa() {
            return ba;
        }

        public void setBa(byte[] ba) {
            this.ba = ba;
        }

    }
}
