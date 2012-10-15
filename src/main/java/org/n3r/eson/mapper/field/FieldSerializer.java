package org.n3r.eson.mapper.field;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;
import org.n3r.eson.parser.FieldInfo;

public abstract class FieldSerializer implements Comparable<FieldSerializer> {
    protected final FieldInfo fieldInfo;
    private boolean writeNull = false;

    public FieldSerializer(FieldInfo fieldInfo) {
        super();
        this.fieldInfo = fieldInfo;
        fieldInfo.setAccessible(true);
    }

    public boolean isWriteNull() {
        return writeNull;
    }

    public Field getField() {
        return fieldInfo.getField();
    }

    public String getName() {
        return fieldInfo.getName();
    }

    public Method getMethod() {
        return fieldInfo.getMethod();
    }

    public void writePrefix(EsonSerializer serializer) throws IOException {
        SerializeWriter writer = serializer.getWriter();

        writer.writeKey(fieldInfo.getName());
    }

    @Override
    public int compareTo(FieldSerializer o) {
        return getName().compareTo(o.getName());
    }

    public Object getPropertyValue(Object object) throws Exception {
        return fieldInfo.get(object);
    }

    public abstract void writeProperty(EsonSerializer serializer, Object propertyValue) throws Exception;

}
