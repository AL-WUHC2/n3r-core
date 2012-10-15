package org.n3r.eson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import org.n3r.eson.utils.TypeUtils;

public class JSONArray extends JSON implements List<Object>, Cloneable, RandomAccess, Serializable {

    private static final long serialVersionUID = 1L;
    private final List<Object> list;
    protected transient Object relatedArray;
    protected transient Type componentType;

    public JSONArray() {
        list = new ArrayList<Object>(10);
    }

    public JSONArray(List<Object> list) {
        this.list = list;
    }

    public JSONArray(int initialCapacity) {
        list = new ArrayList<Object>(initialCapacity);
    }

    public Object getRelatedArray() {
        return relatedArray;
    }

    public void setRelatedArray(Object relatedArray) {
        this.relatedArray = relatedArray;
    }

    public Type getComponentType() {
        return componentType;
    }

    public void setComponentType(Type componentType) {
        this.componentType = componentType;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(Object e) {
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Object set(int index, Object element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        list.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public Object get(int index) {
        return list.get(index);
    }

    public JSONObject getJSONObject(int index) {
        Object value = list.get(index);

        if (value instanceof JSONObject) return (JSONObject) value;

        return (JSONObject) toJSON(value);
    }

    public JSONArray getJSONArray(int index) {
        Object value = list.get(index);

        if (value instanceof JSONArray) return (JSONArray) value;

        return (JSONArray) toJSON(value);
    }

    public <T> T getObject(int index, Class<T> clazz) {
        Object obj = list.get(index);
        return TypeUtils.castToJavaBean(obj, clazz);
    }

    public Boolean getBoolean(int index) {
        Object value = get(index);

        if (value == null) return null;

        return TypeUtils.castToBoolean(value);
    }

    public boolean getBooleanValue(int index) {
        Object value = get(index);

        if (value == null) return false;

        return TypeUtils.castToBoolean(value).booleanValue();
    }

    public Byte getByte(int index) {
        Object value = get(index);

        return TypeUtils.castToByte(value);
    }

    public byte getByteValue(int index) {
        Object value = get(index);

        if (value == null) return 0;

        return TypeUtils.castToByte(value).byteValue();
    }

    public Short getShort(int index) {
        Object value = get(index);

        return TypeUtils.castToShort(value);
    }

    public short getShortValue(int index) {
        Object value = get(index);

        if (value == null) return 0;

        return TypeUtils.castToShort(value).shortValue();
    }

    public Integer getInteger(int index) {
        Object value = get(index);

        return TypeUtils.castToInt(value);
    }

    public int getIntValue(int index) {
        Object value = get(index);

        if (value == null) return 0;

        return TypeUtils.castToInt(value).intValue();
    }

    public Long getLong(int index) {
        Object value = get(index);

        return TypeUtils.castToLong(value);
    }

    public long getLongValue(int index) {
        Object value = get(index);

        if (value == null) return 0L;

        return TypeUtils.castToLong(value).longValue();
    }

    public Float getFloat(int index) {
        Object value = get(index);

        return TypeUtils.castToFloat(value);
    }

    public float getFloatValue(int index) {
        Object value = get(index);

        if (value == null) return 0F;

        return TypeUtils.castToFloat(value).floatValue();
    }

    public Double getDouble(int index) {
        Object value = get(index);

        return TypeUtils.castToDouble(value);
    }

    public double getDoubleValue(int index) {
        Object value = get(index);

        if (value == null) return 0D;

        return TypeUtils.castToDouble(value).floatValue();
    }

    public BigDecimal getBigDecimal(int index) {
        Object value = get(index);

        return TypeUtils.castToBigDecimal(value);
    }

    public BigInteger getBigInteger(int index) {
        Object value = get(index);

        return TypeUtils.castToBigInteger(value);
    }

    public String getString(int index) {
        Object value = get(index);

        return TypeUtils.castToString(value);
    }

    public java.util.Date getDate(int index) {
        Object value = get(index);

        return TypeUtils.castToDate(value);
    }

    public java.sql.Date getSqlDate(int index) {
        Object value = get(index);

        return TypeUtils.castToSqlDate(value);
    }

    public java.sql.Timestamp getTimestamp(int index) {
        Object value = get(index);

        return TypeUtils.castToTimestamp(value);
    }

    @Override
    public Object clone() {
        return new JSONArray(new ArrayList<Object>(list));
    }

    @Override
    public boolean equals(Object obj) {
        return list.equals(obj);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

}
