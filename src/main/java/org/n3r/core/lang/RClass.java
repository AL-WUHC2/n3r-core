package org.n3r.core.lang;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class RClass {

    public static boolean isAssignable(Class<?> fromClass, Class<?>... toClasses) {
        for (Class<?> toClass : toClasses)
            if (ClassUtils.isAssignable(fromClass, toClass)) return true;

        return false;
    }

    public static Class<?> findClass(String className) {
        if (StringUtils.isBlank(className)) return null;

        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            // Ignore
        }

        return null;
    }

}
