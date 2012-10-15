package org.n3r.eson.mapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.HashMap;

import org.n3r.eson.mapper.serializer.ClassSerializer;
import org.n3r.eson.mapper.serializer.DateSerializer;
import org.n3r.eson.mapper.serializer.JavaBeanSerializer;
import org.n3r.eson.mapper.serializer.MapSerializer;
import org.n3r.eson.mapper.serializer.ObjectArraySerializer;
import org.n3r.eson.mapper.serializer.PrimitivesSerializer;
import org.n3r.eson.mapper.serializer.StringSerializer;
import org.n3r.eson.utils.IdentityHashMap;

public class SerializerMapping extends IdentityHashMap<Type, EsonSerializable> {
    public final static SerializerMapping instance = new SerializerMapping();

    public SerializerMapping() {
        put(Boolean.class, PrimitivesSerializer.BooleanSerializer.instance);
        put(Character.class, PrimitivesSerializer.CharacterSerializer.instance);
        put(Byte.class, PrimitivesSerializer.ByteSerializer.instance);
        put(Short.class, PrimitivesSerializer.ShortSerializer.instance);
        put(Integer.class, PrimitivesSerializer.IntegerSerializer.instance);
        put(Long.class, PrimitivesSerializer.LongSerializer.instance);
        put(Float.class, PrimitivesSerializer.FloatSerializer.instance);
        put(Double.class, PrimitivesSerializer.DoubleSerializer.instance);
        put(BigDecimal.class, PrimitivesSerializer.BigDecimalSerializer.instance);
        put(BigInteger.class, PrimitivesSerializer.BigIntegerSerializer.instance);

        put(String.class, StringSerializer.instance);

        put(byte[].class, PrimitivesSerializer.ByteArraySerializer.instance);
        put(short[].class, PrimitivesSerializer.ShortArraySerializer.instance);
        put(int[].class, PrimitivesSerializer.IntArraySerializer.instance);
        put(long[].class, PrimitivesSerializer.LongArraySerializer.instance);
        put(float[].class, PrimitivesSerializer.FloatArraySerializer.instance);
        put(double[].class, PrimitivesSerializer.DoubleArraySerializer.instance);
        put(boolean[].class, PrimitivesSerializer.BooleanArraySerializer.instance);
        put(char[].class, PrimitivesSerializer.CharArraySerializer.instance);

        put(Object[].class, ObjectArraySerializer.instance);
        put(Class.class, ClassSerializer.instance);

        put(HashMap.class, MapSerializer.instance);
        put(Date.class, DateSerializer.instance);
    }

    public EsonSerializable createJavaBeanSerializer(Class<?> clazz) {
        JavaBeanSerializer serializer = new JavaBeanSerializer(clazz);
        put(clazz, serializer);

        return serializer;
    }

}
