package org.n3r.eson;

import junit.framework.TestCase;

import org.n3r.core.lang.RBaseBean;

public class LongTest extends TestCase {

    public void test_0() throws Exception {
        VO vo = new VO();
        vo.setValue(33L);

        String text = new Eson().toString(vo);

        assertEquals("{value:33}", text);
        assertEquals(vo, new Eson().parse(text, VO.class));
    }

    public static class VO extends RBaseBean {

        private Long value;

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}
