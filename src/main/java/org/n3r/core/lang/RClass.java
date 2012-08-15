package org.n3r.core.lang;

import org.apache.commons.lang3.ClassUtils;

public class RClass {

    public static boolean isAssignable(Class<?> fromClass, Class<?>... toClasses) {
        for (Class<?> toClass : toClasses) {
            if (ClassUtils.isAssignable(fromClass, toClass)) return true;
        }

        return false;
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (Exception e) {
            return null;
        }
    }

}
