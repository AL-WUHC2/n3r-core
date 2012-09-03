package org.n3r.core.lang;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.base.Throwables;

public class RBean {

    public static String getProperty(Object bean, String name) {
        try {
            return BeanUtils.getProperty(bean, name);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static Object getPropertyQuietly(Object bean, String name) {
        try {
            return PropertyUtils.getProperty(bean, name);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getStrPropertyQuietly(Object bean, String name) {
        try {
            return BeanUtils.getProperty(bean, name);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setPropertyQuietly(Object bean, String name, Object value) {
        try {
            BeanUtils.setProperty(bean, name, value);
        } catch (Exception e) {}
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object bean) {
        if (bean == null) return new HashMap<String, Object>();
        if (bean instanceof Map) return (Map<String, Object>) bean;

        try {
            return BeanUtils.describe(bean);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
