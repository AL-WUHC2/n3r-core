package org.n3r.eson.mapper.serializer;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;

public class ClassSerializer implements EsonSerializable {
    public final static ClassSerializer instance = new ClassSerializer();

    @Override
    @SuppressWarnings("rawtypes")
    public void write(EsonSerializer serializer, Object object) {
        SerializeWriter out = serializer.getWriter();

        Class clazz = (Class) object;
        out.writeValue(clazz.getName());
    }

}
