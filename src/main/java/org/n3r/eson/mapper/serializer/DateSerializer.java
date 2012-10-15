package org.n3r.eson.mapper.serializer;

import java.util.Date;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;

public class DateSerializer implements EsonSerializable {
    public final static DateSerializer instance = new DateSerializer();

    @Override
    public void write(EsonSerializer serializer, Object object) {
        SerializeWriter writer = serializer.getWriter();

        if (object == null) writer.writeNull();
        else writer.writeLong(((Date) object).getTime());
    }
}
