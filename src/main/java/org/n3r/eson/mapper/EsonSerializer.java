package org.n3r.eson.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.n3r.eson.EsonFeature;
import org.n3r.eson.mapper.serializer.DateSerializer;
import org.n3r.eson.mapper.serializer.ListSerializer;
import org.n3r.eson.mapper.serializer.MapSerializer;

public class EsonSerializer {
    private final SerializerMapping mapping;
    private final SerializeWriter writer;

    private int indentCount;
    private String indent = "\t";
    private boolean usePrettyFormat;

    public EsonSerializer(SerializeWriter writer, SerializerMapping mapping) {
        this.writer = writer;
        this.mapping = mapping;
        usePrettyFormat = writer.isEnabled(EsonFeature.PrettyFormat);
    }

    public void write(Object object) {
        if (object == null) {
            writer.writeNull();
            return;
        }

        Class<?> clazz = object.getClass();
        EsonSerializable serializer = getSerializer(clazz);

        serializer.write(this, object);
    }

    public EsonSerializable getSerializer(Class<?> clazz) {
        EsonSerializable serializer = mapping.get(clazz);
        if (serializer != null) return serializer;

        if (Map.class.isAssignableFrom(clazz)) serializer = MapSerializer.instance;
        else if (List.class.isAssignableFrom(clazz)) serializer = ListSerializer.instance;
        else if (Date.class.isAssignableFrom(clazz)) serializer = DateSerializer.instance;
        else serializer = mapping.createJavaBeanSerializer(clazz);

        return serializer;
    }

    public SerializeWriter getWriter() {
        return writer;
    }

    public void incrementIndentAndPrintln() {
        ++indentCount;
        println();
    }

    public void incrementIndent() {
        ++indentCount;
    }

    public void decrementIdentAndPrintln() {
        --indentCount;
        println();
    }

    public void println() {
        if (!usePrettyFormat) return;

        writer.write('\n');
        for (int i = 0; i < indentCount; ++i)
            writer.write(indent);
    }

    public void printlnWhenIdent() {
        if (indentCount > 0) println();
    }

    public void setUsePrettyFormat(boolean usePrettyFormat) {
        if (!usePrettyFormat) this.usePrettyFormat = false;
    }

}
