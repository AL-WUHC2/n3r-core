package org.n3r.eson.mapper.serializer;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;

public class ObjectArraySerializer implements EsonSerializable {
    public static final ObjectArraySerializer instance = new ObjectArraySerializer();

    @Override
    public final void write(EsonSerializer serializer, Object object) {
        SerializeWriter out = serializer.getWriter();

        Object[] array = (Object[]) object;

        if (object == null) {
            out.writeNull();
            return;
        }

        int size = array.length;

        int end = size - 1;

        if (end == -1) {
            out.write("[]");
            return;
        }

        Class<?> preClazz = null;
        EsonSerializable preWriter = null;
        out.write('[');

        for (int i = 0; i < end; ++i) {
            Object item = array[i];

            if (item == null) out.write("null,");
            else {
                Class<?> clazz = item.getClass();

                if (clazz == preClazz) preWriter.write(serializer, item);
                else {
                    preClazz = clazz;
                    preWriter = serializer.getSerializer(clazz);

                    preWriter.write(serializer, item);
                }

                out.write(',');
            }
        }

        Object item = array[end];

        if (item == null) out.write("null]");
        else {
            serializer.write(item);
            out.write(']');
        }
    }
}
