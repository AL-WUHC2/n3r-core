package org.n3r.eson.mapper.serializer;

import java.lang.reflect.Type;
import java.util.List;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;

public final class ListSerializer implements EsonSerializable {
    public static final ListSerializer instance = new ListSerializer();

    @Override
    public final void write(EsonSerializer serializer, Object object) {
        SerializeWriter writer = serializer.getWriter();

        Type elementType = null;

        if (object == null) {
            writer.writeNull();
            return;
        }

        List<?> list = (List<?>) object;

        final int size = list.size();
        int end = size - 1;

        if (end == -1) {
            writer.write("[]");
            return;
        }

        writer.write('[');
        for (int i = 0; i < end; ++i)
            writeItem(serializer, writer, elementType, list, i, ',');

        writeItem(serializer, writer, elementType, list, end, ']');
    }

    protected void writeItem(EsonSerializer serializer, SerializeWriter writer,
            Type elementType, List<?> list, int end, char endChar) {
        Object item = list.get(end);

        if (item == null) writer.write("null]");
        else {
            Class<?> clazz = item.getClass();

            if (clazz == Integer.class) writer.writeIntAndChar(((Integer) item).intValue(), endChar);
            else if (clazz == Long.class) writer.writeLongAndChar(((Long) item).longValue(), endChar);
            else {
                EsonSerializable itemSerializer = serializer.getSerializer(item.getClass());
                itemSerializer.write(serializer, item);
                writer.write(endChar);
            }
        }
    }

}
