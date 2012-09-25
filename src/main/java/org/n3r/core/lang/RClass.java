package org.n3r.core.lang;

import java.lang.reflect.Modifier;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class RClass {

    public static boolean isAssignable(Class<?> fromClass, Class<?>... toClasses) {
        for (Class<?> toClass : toClasses)
            if (ClassUtils.isAssignable(fromClass, toClass)) return true;

        return false;
    }

    public static boolean isConcrete(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
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

    /**
     * Load a class given its name. BL: We wan't to use a known
     * ClassLoader--hopefully the heirarchy is set correctly.
     * @param <T> 类型
     * @param className        A class name
     * @return The class pointed to by <code>className</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> loadClass(String className) {
        try {
            return (Class<T>) getClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e) {
            // Ignore
        }
        return null;
    }

    /**
     * Return the context classloader. BL: if this is command line operation,
     * the classloading issues are more sane. During servlet execution, we
     * explicitly set the ClassLoader.
     * 
     * @return The context classloader.
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
