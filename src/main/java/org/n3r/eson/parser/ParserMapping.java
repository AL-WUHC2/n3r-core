package org.n3r.eson.parser;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.n3r.eson.parser.deserializer.*;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.BigDecimalDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.BigIntegerDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.BooleanDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.CharacterDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.FloatDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.IntegerDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.LongDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.NumberDeserializer;
import org.n3r.eson.parser.field.ArrayListStringFieldDeserializer;
import org.n3r.eson.parser.field.ArrayListTypeFieldDeserializer;
import org.n3r.eson.parser.field.BooleanFieldDeserializer;
import org.n3r.eson.parser.field.DefaultFieldDeserializer;
import org.n3r.eson.parser.field.IntegerFieldDeserializer;
import org.n3r.eson.parser.field.LongFieldDeserializer;
import org.n3r.eson.parser.field.StringFieldDeserializer;
import org.n3r.eson.utils.IdentityHashMap;

public class ParserMapping {

    public static ParserMapping getGlobalInstance() {
        return global;
    }

    private final Set<Class<?>> primitiveClasses = new HashSet<Class<?>>();

    private static ParserMapping global = new ParserMapping();

    private final IdentityHashMap<Type, EsonDeserializable> derializers = new IdentityHashMap<Type, EsonDeserializable>();

    private ObjectDeserializer defaultSerializer = new ObjectDeserializer();

    protected final SymbolTable symbolTable = new SymbolTable();

    public ObjectDeserializer getDefaultSerializer() {
        return defaultSerializer;
    }

    public ParserMapping() {
        primitiveClasses.add(boolean.class);
        primitiveClasses.add(Boolean.class);
        primitiveClasses.add(char.class);
        primitiveClasses.add(Character.class);
        primitiveClasses.add(byte.class);
        primitiveClasses.add(Byte.class);
        primitiveClasses.add(short.class);
        primitiveClasses.add(Short.class);
        primitiveClasses.add(int.class);
        primitiveClasses.add(Integer.class);
        primitiveClasses.add(long.class);
        primitiveClasses.add(Long.class);
        primitiveClasses.add(float.class);
        primitiveClasses.add(Float.class);
        primitiveClasses.add(double.class);
        primitiveClasses.add(Double.class);

        primitiveClasses.add(BigInteger.class);
        primitiveClasses.add(BigDecimal.class);

        primitiveClasses.add(String.class);
        primitiveClasses.add(java.util.Date.class);
        primitiveClasses.add(java.sql.Date.class);
        primitiveClasses.add(java.sql.Time.class);
        primitiveClasses.add(java.sql.Timestamp.class);

        //        derializers.put(SimpleDateFormat.class, DateFormatDeserializer.instance);
        //        derializers.put(java.sql.Timestamp.class, TimestampDeserializer.instance);
        //        derializers.put(java.sql.Date.class, SqlDateDeserializer.instance);
        //        derializers.put(java.sql.Time.class, TimeDeserializer.instance);
        derializers.put(java.util.Date.class, DateDeserializer.instance);
        //        derializers.put(Calendar.class, CalendarDeserializer.instance);
        //
        //        derializers.put(JSONObject.class, JSONObjectDeserializer.instance);
        //        derializers.put(JSONArray.class, JSONArrayDeserializer.instance);

        derializers.put(Map.class, MapDeserializer.instance);
        derializers.put(HashMap.class, MapDeserializer.instance);
        derializers.put(LinkedHashMap.class, MapDeserializer.instance);
        derializers.put(TreeMap.class, MapDeserializer.instance);
        derializers.put(ConcurrentMap.class, MapDeserializer.instance);
        derializers.put(ConcurrentHashMap.class, MapDeserializer.instance);

        //        derializers.put(Collection.class, CollectionDeserializer.instance);
        //        derializers.put(List.class, CollectionDeserializer.instance);
        //        derializers.put(ArrayList.class, CollectionDeserializer.instance);
        //
        //        derializers.put(Collection.class, CollectionDeserializer.instance);
        //        derializers.put(List.class, CollectionDeserializer.instance);
        //        derializers.put(ArrayList.class, CollectionDeserializer.instance);

        //        derializers.put(Object.class, JavaObjectDeserializer.instance);
        //        derializers.put(String.class, StringDeserializer.instance);
        derializers.put(char.class, CharacterDeserializer.instance);
        derializers.put(Character.class, CharacterDeserializer.instance);
        derializers.put(byte.class, NumberDeserializer.instance);
        derializers.put(Byte.class, NumberDeserializer.instance);
        derializers.put(short.class, NumberDeserializer.instance);
        derializers.put(Short.class, NumberDeserializer.instance);
        derializers.put(int.class, IntegerDeserializer.instance);
        derializers.put(Integer.class, IntegerDeserializer.instance);
        derializers.put(long.class, LongDeserializer.instance);
        derializers.put(Long.class, LongDeserializer.instance);
        derializers.put(BigInteger.class, BigIntegerDeserializer.instance);
        derializers.put(BigDecimal.class, BigDecimalDeserializer.instance);
        derializers.put(float.class, FloatDeserializer.instance);
        derializers.put(Float.class, FloatDeserializer.instance);
        derializers.put(double.class, NumberDeserializer.instance);
        derializers.put(Double.class, NumberDeserializer.instance);
        derializers.put(boolean.class, BooleanDeserializer.instance);
        derializers.put(Boolean.class, BooleanDeserializer.instance);
        derializers.put(Class.class, ClassDerializer.instance);
        derializers.put(char[].class, CharArrayDeserializer.instance);

        derializers.put(Map.class, MapDeserializer.instance);
        derializers.put(HashMap.class, MapDeserializer.instance);
        derializers.put(LinkedHashMap.class, MapDeserializer.instance);
        derializers.put(TreeMap.class, MapDeserializer.instance);
        derializers.put(ConcurrentMap.class, MapDeserializer.instance);
        derializers.put(ConcurrentHashMap.class, MapDeserializer.instance);
        derializers.put(String.class, StringDeserializer.instance);

    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public IdentityHashMap<Type, EsonDeserializable> getDerializers() {
        return derializers;
    }

    public EsonDeserializable getDeserializer(Type type) {
        EsonDeserializable derializer = derializers.get(type);
        if (derializer != null) return derializer;

        if (type instanceof Class<?>) return getDeserializer((Class<?>) type, type);

        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType instanceof Class<?>) return getDeserializer((Class<?>) rawType, type);
            else return getDeserializer(rawType);
        }

        return defaultSerializer;
    }

    public EsonDeserializable getDeserializer(Class<?> clazz, Type type) {
        EsonDeserializable deserializer = derializers.get(type);
        if (deserializer != null) return deserializer;

        if (type == null) type = clazz;

        deserializer = derializers.get(type);
        if (deserializer != null) return deserializer;

        if (type instanceof WildcardType || type instanceof TypeVariable) deserializer = derializers.get(clazz);

        if (deserializer != null) return deserializer;

        deserializer = derializers.get(type);
        if (deserializer != null) return deserializer;

        deserializer = createJavaBeanDeserializer(clazz, type);
        derializers.put(type, deserializer);

        return deserializer;
    }

    public EsonDeserializable createJavaBeanDeserializer(Class<?> clazz, Type type) {
        if (clazz == Class.class) return defaultSerializer;

        return new JavaBeanDeserializer(this, clazz, type);

    }

    public FieldDeserializer createFieldDeserializer(ParserMapping mapping, Class<?> clazz, FieldInfo fieldInfo) {

        return createFieldDeserializerWithoutASM(mapping, clazz, fieldInfo);

    }

    public FieldDeserializer createFieldDeserializerWithoutASM(ParserMapping mapping, Class<?> clazz,
            FieldInfo fieldInfo) {
        Class<?> fieldClass = fieldInfo.getFieldClass();

        if (fieldClass == boolean.class || fieldClass == Boolean.class)
            return new BooleanFieldDeserializer(mapping, clazz, fieldInfo);

        if (fieldClass == int.class || fieldClass == Integer.class)
            return new IntegerFieldDeserializer(mapping, clazz, fieldInfo);

        if (fieldClass == long.class || fieldClass == Long.class)
            return new LongFieldDeserializer(mapping, clazz, fieldInfo);

        if (fieldClass == String.class) return new StringFieldDeserializer(mapping, clazz, fieldInfo);

        if (fieldClass == List.class || fieldClass == ArrayList.class) {
            Type fieldType = fieldInfo.getFieldType();
            if (fieldType instanceof ParameterizedType) {
                Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
                if (itemType == String.class) return new ArrayListStringFieldDeserializer(mapping, clazz, fieldInfo);
            }

            return new ArrayListTypeFieldDeserializer(mapping, clazz, fieldInfo);
        }

        return new DefaultFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public void putDeserializer(Type type, EsonDeserializable deserializer) {
        derializers.put(type, deserializer);
    }

    public EsonDeserializable getDeserializer(FieldInfo fieldInfo) {
        return getDeserializer(fieldInfo.getFieldClass(), fieldInfo.getFieldType());
    }

    public boolean isPrimitive(Class<?> clazz) {
        return primitiveClasses.contains(clazz);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, FieldDeserializer> getFieldDeserializers(Class<?> clazz) {
        EsonDeserializable deserizer = getDeserializer(clazz);

        if (deserizer instanceof JavaBeanDeserializer) return ((JavaBeanDeserializer) deserizer)
                .getFieldDeserializerMap();
        else return Collections.emptyMap();
    }

}
