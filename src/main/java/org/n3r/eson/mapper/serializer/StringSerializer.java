package org.n3r.eson.mapper.serializer;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;

public class StringSerializer implements EsonSerializable {

    public static StringSerializer instance = new StringSerializer();

    @Override
    public void write(EsonSerializer serializer, Object object) {
        write(serializer, (String) object);
    }

    public void write(EsonSerializer serializer, String value) {
        SerializeWriter out = serializer.getWriter();

        if (value == null) out.writeNull();
        else out.writeValue(value);
    }
}
