package org.n3r.eson.mapper.serializer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;

public class SimpleDateFormatSerializer implements EsonSerializable {
    private final String pattern;

    public SimpleDateFormatSerializer(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void write(EsonSerializer serializer, Object object) {
        if (object == null) {
            serializer.getWriter().writeNull();
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat(pattern);

        String text = format.format((Date) object);
        serializer.write(text);
    }
}
