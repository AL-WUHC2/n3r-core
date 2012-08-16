package org.n3r.core.lang;

import org.apache.commons.lang3.ClassUtils;

public class RClass {

    public static boolean isAssignable(Class<?> fromClass, Class<?>... toClasses) {
        for (Class<?> toClass : toClasses) {
            if (ClassUtils.isAssignable(fromClass, toClass)) return true;
        }

        return false;
    }
}
