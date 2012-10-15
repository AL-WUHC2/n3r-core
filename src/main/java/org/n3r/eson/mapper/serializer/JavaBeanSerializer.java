package org.n3r.eson.mapper.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.n3r.eson.EsonException;
import org.n3r.eson.EsonFeature;
import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;
import org.n3r.eson.mapper.field.FieldSerializer;
import org.n3r.eson.mapper.field.NumberFieldSerializer;
import org.n3r.eson.mapper.field.ObjectFieldSerializer;
import org.n3r.eson.parser.FieldInfo;
import org.n3r.eson.utils.TypeUtils;

public class JavaBeanSerializer implements EsonSerializable {
    private final FieldSerializer[] getters;
    private final FieldSerializer[] sortedGetters;

    public JavaBeanSerializer(Class<?> clazz) {
        this(clazz, (Map<String, String>) null);
    }

    public JavaBeanSerializer(Class<?> clazz, Map<String, String> aliasMap) {
        {
            List<FieldSerializer> getterList = new ArrayList<FieldSerializer>();
            List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(clazz, aliasMap, false);

            for (FieldInfo fieldInfo : fieldInfoList)
                getterList.add(createFieldSerializer(fieldInfo));

            getters = getterList.toArray(new FieldSerializer[getterList.size()]);
        }
        {
            List<FieldSerializer> getterList = new ArrayList<FieldSerializer>();
            List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(clazz, aliasMap, true);

            for (FieldInfo fieldInfo : fieldInfoList)
                getterList.add(createFieldSerializer(fieldInfo));

            sortedGetters = getterList.toArray(new FieldSerializer[getterList.size()]);
        }
    }

    @Override
    public void write(EsonSerializer serializer, Object object) {
        SerializeWriter writer = serializer.getWriter();

        if (object == null) {
            writer.writeNull();
            return;
        }

        final FieldSerializer[] getters;

        getters = writer.isEnabled(EsonFeature.SortKey) ? sortedGetters : this.getters;

        try {
            serializer.setUsePrettyFormat(getters.length > 0);
            serializer.printlnWhenIdent();
            writer.write('{');
            serializer.incrementIndentAndPrintln();

            boolean commaFlag = false;

            boolean skipTransientField = writer.isEnabled(EsonFeature.SkipTransientField);
            for (FieldSerializer fieldSerializer : getters) {
                if (skipTransientField) {
                    Field field = fieldSerializer.getField();
                    if (field != null && Modifier.isTransient(field.getModifiers())) continue;
                }

                Object propertyValue = fieldSerializer.getPropertyValue(object);
                String key = fieldSerializer.getName();

                if (commaFlag) {
                    writer.write(',');
                    serializer.println();
                }

                if (key != fieldSerializer.getName()) {
                    writer.writeKey(key);
                    serializer.write(propertyValue);
                } else fieldSerializer.writeProperty(serializer, propertyValue);

                commaFlag = true;
            }

            serializer.decrementIdentAndPrintln();

            writer.write('}');
        } catch (Exception e) {
            throw new EsonException("write javaBean error", e);
        }
    }

    public FieldSerializer createFieldSerializer(FieldInfo fieldInfo) {
        Class<?> clazz = fieldInfo.getFieldClass();

        if (clazz == Number.class) return new NumberFieldSerializer(fieldInfo);

        return new ObjectFieldSerializer(fieldInfo);
    }
}
