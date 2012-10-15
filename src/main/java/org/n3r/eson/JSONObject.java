package org.n3r.eson;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.n3r.eson.annotation.EsonField;
import org.n3r.eson.parser.ParserMapping;
import org.n3r.eson.utils.AntiCollisionHashMap;
import org.n3r.eson.utils.TypeUtils;

import static org.n3r.eson.utils.TypeUtils.*;

public class JSONObject extends JSON implements Map<String, Object>, Cloneable, Serializable, InvocationHandler {
    private static final long serialVersionUID = -6568049834258710010L;

    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private final Map<String, Object> map;

    public JSONObject() {
        this(DEFAULT_INITIAL_CAPACITY, false);
    }

    public JSONObject(Map<String, Object> map) {
        this.map = map;
    }

    public JSONObject(boolean ordered) {
        this(DEFAULT_INITIAL_CAPACITY, ordered);
    }

    public JSONObject(int initialCapacity) {
        this(initialCapacity, false);
    }

    public JSONObject(int initialCapacity, boolean ordered) {
        if (ordered) map = new LinkedHashMap<String, Object>(initialCapacity);
        else map = new AntiCollisionHashMap<String, Object>(initialCapacity);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    public JSONObject getJSONObject(String key) {
        Object value = map.get(key);

        if (value instanceof JSONObject) return (JSONObject) value;

        return (JSONObject) toJSON(value);
    }

    public JSONArray getJSONArray(String key) {
        Object value = map.get(key);

        if (value instanceof JSONArray) return (JSONArray) value;

        return (JSONArray) toJSON(value);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        Object obj = map.get(key);
        return TypeUtils.castToJavaBean(obj, clazz);
    }

    public Boolean getBoolean(String key) {
        Object value = get(key);

        if (value == null) return null;

        return castToBoolean(value);
    }

    public byte[] getBytes(String key) {
        Object value = get(key);

        if (value == null) return null;

        return castToBytes(value);
    }

    public boolean getBooleanValue(String key) {
        Object value = get(key);

        if (value == null) return false;

        return castToBoolean(value).booleanValue();
    }

    public Byte getByte(String key) {
        Object value = get(key);

        return castToByte(value);
    }

    public byte getByteValue(String key) {
        Object value = get(key);

        if (value == null) return 0;

        return castToByte(value).byteValue();
    }

    public Short getShort(String key) {
        Object value = get(key);

        return castToShort(value);
    }

    public short getShortValue(String key) {
        Object value = get(key);

        if (value == null) return 0;

        return castToShort(value).shortValue();
    }

    public Integer getInteger(String key) {
        Object value = get(key);

        return castToInt(value);
    }

    public int getIntValue(String key) {
        Object value = get(key);

        if (value == null) return 0;

        return castToInt(value).intValue();
    }

    public Long getLong(String key) {
        Object value = get(key);

        return castToLong(value);
    }

    public long getLongValue(String key) {
        Object value = get(key);

        if (value == null) return 0L;

        return castToLong(value).longValue();
    }

    public Float getFloat(String key) {
        Object value = get(key);

        return castToFloat(value);
    }

    public float getFloatValue(String key) {
        Object value = get(key);

        if (value == null) return 0F;

        return castToFloat(value).floatValue();
    }

    public Double getDouble(String key) {
        Object value = get(key);

        return castToDouble(value);
    }

    public double getDoubleValue(String key) {
        Object value = get(key);

        if (value == null) return 0D;

        return castToDouble(value).floatValue();
    }

    public BigDecimal getBigDecimal(String key) {
        Object value = get(key);

        return castToBigDecimal(value);
    }

    public BigInteger getBigInteger(String key) {
        Object value = get(key);

        return castToBigInteger(value);
    }

    public String getString(String key) {
        Object value = get(key);

        if (value == null) return null;

        return value.toString();
    }

    public Date getDate(String key) {
        Object value = get(key);

        return castToDate(value);
    }

    public java.sql.Date getSqlDate(String key) {
        Object value = get(key);

        return castToSqlDate(value);
    }

    public java.sql.Timestamp getTimestamp(String key) {
        Object value = get(key);

        return castToTimestamp(value);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Object clone() {
        return new JSONObject(new HashMap<String, Object>(map));
    }

    @Override
    public boolean equals(Object obj) {
        return map.equals(obj);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1) {
            Class<?> returnType = method.getReturnType();
            if (returnType != void.class) throw new EsonException("illegal setter");

            String name = null;
            EsonField annotation = method.getAnnotation(EsonField.class);
            if (annotation != null) if (annotation.name().length() != 0) name = annotation.name();

            if (name == null) {
                name = method.getName();
                if (!name.startsWith("set")) throw new EsonException("illegal setter");

                name = name.substring(3);
                if (name.length() == 0) throw new EsonException("illegal setter");
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }

            map.put(name, args[0]);
            return null;
        }

        if (parameterTypes.length == 0) {
            Class<?> returnType = method.getReturnType();
            if (returnType == void.class) throw new EsonException("illegal getter");

            String name = null;
            EsonField esonField = method.getAnnotation(EsonField.class);
            if (esonField != null) if (esonField.name().length() != 0) name = esonField.name();

            if (name == null) {
                name = method.getName();
                if (name.startsWith("get")) {
                    name = name.substring(3);
                    if (name.length() == 0) throw new EsonException("illegal getter");
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                } else if (name.startsWith("is")) {
                    name = name.substring(2);
                    if (name.length() == 0) throw new EsonException("illegal getter");
                    name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                } else throw new EsonException("illegal getter");
            }

            Object value = map.get(name);
            return TypeUtils.cast(value, method.getGenericReturnType(), ParserMapping.getGlobalInstance());
        }

        throw new UnsupportedOperationException(method.toGenericString());
    }

}
