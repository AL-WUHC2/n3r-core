package org.n3r.eson.parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import org.n3r.eson.utils.TypeUtils;

public class FieldInfo implements Comparable<FieldInfo> {

    private final String name;
    private final Method method;
    private final Field field;

    private final Class<?> fieldClass;
    private final Type fieldType;
    private final Class<?> declaringClass;
    private boolean getOnly = false;

    public FieldInfo(String name, Class<?> declaringClass, Class<?> fieldClass, Type fieldType, Method method,
            Field field) {
        this.name = name;
        this.declaringClass = declaringClass;
        this.fieldClass = fieldClass;
        this.fieldType = fieldType;
        this.method = method;
        this.field = field;

        if (method != null) method.setAccessible(true);

        if (field != null) field.setAccessible(true);
    }

    public FieldInfo(String name, Method method, Field field) {
        this(name, method, field, null, null);
    }

    public FieldInfo(String name, Method method, Field field, Class<?> clazz, Type type) {
        this.name = name;
        this.method = method;
        this.field = field;

        if (method != null) method.setAccessible(true);

        if (field != null) field.setAccessible(true);

        Type fieldType;
        Class<?> fieldClass;
        if (method != null) {
            if (method.getParameterTypes().length == 1) {
                fieldClass = method.getParameterTypes()[0];
                fieldType = method.getGenericParameterTypes()[0];
            } else {
                fieldClass = method.getReturnType();
                fieldType = method.getGenericReturnType();
                getOnly = true;
            }
            declaringClass = method.getDeclaringClass();
        } else {
            fieldClass = field.getType();
            fieldType = field.getGenericType();
            declaringClass = field.getDeclaringClass();
        }

        Type genericFieldType = getFieldType(clazz, type, fieldType);

        if (genericFieldType != fieldType)
            if (genericFieldType instanceof ParameterizedType) fieldClass = TypeUtils.getClass(genericFieldType);
            else if (genericFieldType instanceof Class) fieldClass = TypeUtils.getClass(genericFieldType);

        this.fieldType = genericFieldType;
        this.fieldClass = fieldClass;
    }

    public static Type getFieldType(Class<?> clazz, Type type, Type fieldType) {
        if (clazz == null || type == null) return fieldType;

        if (!(type instanceof ParameterizedType)) return fieldType;

        if (fieldType instanceof TypeVariable) {
            ParameterizedType paramType = (ParameterizedType) type;
            TypeVariable<?> typeVar = (TypeVariable<?>) fieldType;

            for (int i = 0; i < clazz.getTypeParameters().length; ++i)
                if (clazz.getTypeParameters()[i].getName().equals(typeVar.getName())) {
                    fieldType = paramType.getActualTypeArguments()[i];
                    break;
                }
        }

        return fieldType;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        T annotation = null;
        if (method != null) annotation = method.getAnnotation(annotationClass);

        if (annotation == null && field != null) annotation = field.getAnnotation(annotationClass);

        return annotation;
    }

    public Object get(Object javaObject) throws IllegalAccessException, InvocationTargetException {
        if (method != null) {
            Object value = method.invoke(javaObject, new Object[0]);
            return value;
        }

        return field.get(javaObject);
    }

    public void set(Object javaObject, Object value) throws IllegalAccessException, InvocationTargetException {
        if (method != null) {
            method.invoke(javaObject, new Object[] { value });
            return;
        }

        field.set(javaObject, value);
    }

    public void setAccessible(boolean flag) throws SecurityException {
        if (method != null) {
            method.setAccessible(flag);
            return;
        }

        field.setAccessible(flag);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(FieldInfo o) {
        return name.compareTo(o.name);
    }

    public boolean isGetOnly() {
        return getOnly;
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public Class<?> getFieldClass() {
        return fieldClass;
    }

    public Type getFieldType() {
        return fieldType;
    }

    public String getName() {
        return name;
    }

    public Method getMethod() {
        return method;
    }

    public Field getField() {
        return field;
    }

}
