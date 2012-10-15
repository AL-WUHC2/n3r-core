package org.n3r.eson;

import java.util.Date;

import org.junit.Test;
import org.n3r.core.lang.RBaseBean;
import org.n3r.eson.mapper.SerializerMapping;
import org.n3r.eson.mapper.serializer.SimpleDateFormatSerializer;

import static org.junit.Assert.*;

public class DateTest {
    @Test
    public void testDate() {
        Date date = new Date(1348909563000L);
        assertEquals("1348909563000", new Eson().toString(date));

        SerializerMapping serializerMapping = new SerializerMapping();
        serializerMapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
        assertEquals("2012-09-29 17:06:03", new Eson().mapping(serializerMapping).toString(date));

        Date date1 = new Eson().parse("2012-09-29 17:06:03", Date.class);
        assertEquals(date.getTime(), date1.getTime());
        Date date2 = new Eson().parse("1348909563000", Date.class);
        assertEquals(date.getTime(), date2.getTime());

        DateBean dateBean = new DateBean();
        String dateBeanJson = new Eson().mapping(serializerMapping).toString(dateBean);
        assertEquals("{date:null}", dateBeanJson);
        DateBean dateBean2 = new Eson().parse("{date:null}", DateBean.class);
        assertNull(dateBean2.getDate());

        dateBean.setDate(date);
        assertEquals("{date:2012-09-29 17:06:03}", new Eson().mapping(serializerMapping).toString(dateBean));
        assertEquals(dateBean, new Eson().parse("{date:2012-09-29 17:06:03}", DateBean.class));
    }

    public static class DateBean extends RBaseBean {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

    }
}
