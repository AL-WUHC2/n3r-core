package org.n3r.core.lang;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.base.Throwables;

import static java.beans.Introspector.*;
import static org.n3r.core.joor.Reflect.*;
import static org.n3r.core.lang.RClass.*;
import static org.n3r.core.lang.RField.*;

public class RBean {

    public static String getProperty(Object bean, String name) {
        try {
            return BeanUtils.getProperty(bean, name);
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static Object getPropertyQuietly(Object bean, String name) {
        try {
            return PropertyUtils.getProperty(bean, name);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getStrPropertyQuietly(Object bean, String name) {
        try {
            return BeanUtils.getProperty(bean, name);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static void setPropertyQuietly(Object bean, String name, Object value) {
        try {
            BeanUtils.setProperty(bean, name, value);
        }
        catch (Exception e) {}
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object bean) {
        if (bean == null) return new HashMap<String, Object>();
        if (bean instanceof Map) return (Map<String, Object>) bean;

        try {
            return BeanUtils.describe(bean);
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static <T> T beanToAnotherBean(Object bean, Class<T> clazz) {
        try {
            T result = on(clazz).create().get();

            PropertyDescriptor[] pds = getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
            for (PropertyDescriptor pDescriptor : pds) {
                Method method = pDescriptor.getReadMethod();
                if (method == null) continue;

                String fieldName = pDescriptor.getName();
                Field fromField = getTraverseDeclaredField(bean.getClass(), fieldName);
                Field toField = getTraverseDeclaredField(clazz, fieldName);

                if (fromField != null && toField != null && isAssignable(fromField.getType(), toField.getType())) {
                    BeanUtils.setProperty(result, fieldName, method.invoke(bean));
                }
            }
            return result;
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
