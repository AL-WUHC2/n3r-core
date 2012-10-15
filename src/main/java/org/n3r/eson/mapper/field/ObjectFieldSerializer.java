package org.n3r.eson.mapper.field;

import java.util.Collection;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.parser.FieldInfo;

public class ObjectFieldSerializer extends FieldSerializer {
    private EsonSerializable fieldSerializer;

    private Class<?> runtimeFieldClass;
    private boolean writeNumberAsZero = false;
    boolean writeNullStringAsEmpty = false;
    boolean writeNullBooleanAsFalse = false;
    boolean writeNullListAsEmpty = false;
    boolean writeEnumUsingToString = false;

    public ObjectFieldSerializer(FieldInfo fieldInfo) {
        super(fieldInfo);
    }

    @Override
    public void writeProperty(EsonSerializer serializer, Object propertyValue) throws Exception {
        writePrefix(serializer);

        if (fieldSerializer == null) {
            if (propertyValue == null) runtimeFieldClass = getMethod().getReturnType();
            else runtimeFieldClass = propertyValue.getClass();

            fieldSerializer = serializer.getSerializer(runtimeFieldClass);
        }

        if (propertyValue == null) {
            if (writeNumberAsZero && Number.class.isAssignableFrom(runtimeFieldClass)) {
                serializer.getWriter().write('0');
                return;
            } else if (writeNullStringAsEmpty && String.class == runtimeFieldClass) {
                serializer.getWriter().write("\"\"");
                return;
            } else if (writeNullBooleanAsFalse && Boolean.class == runtimeFieldClass) {
                serializer.getWriter().write("false");
                return;
            } else if (writeNullListAsEmpty && Collection.class.isAssignableFrom(runtimeFieldClass)) {
                serializer.getWriter().write("[]");
                return;
            }

            fieldSerializer.write(serializer, null);
            return;
        }

        if (writeEnumUsingToString == true && runtimeFieldClass.isEnum()) {
            serializer.getWriter().write(((Enum<?>) propertyValue).name());
            return;
        }

        Class<?> valueClass = propertyValue.getClass();
        if (valueClass == runtimeFieldClass) {
            fieldSerializer.write(serializer, propertyValue);
            return;
        }

        EsonSerializable valueSerializer = serializer.getSerializer(valueClass);
        valueSerializer.write(serializer, propertyValue);
    }

}
