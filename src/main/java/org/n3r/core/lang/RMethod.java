package org.n3r.core.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.n3r.core.joor.Reflect;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public class RMethod {

    public static void invokeQuietly(Method method, Object object) {
        try {
            method.invoke(object, new Object[] {});
        }
        catch (Exception e) {
            // ingore
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Method method, Object object, Object... args) {
        try {
            Object target = object;
            if (target == null && !Modifier.isStatic(method.getModifiers())) target = Reflect
                    .on(method.getDeclaringClass()).create().get();

            return (T) method.invoke(target, args);
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public static Method findMethod(Class<?> clazz, String methodName) {
        try {
            return clazz.getMethod(methodName, new Class<?>[] {});
        }
        catch (Exception e) {
            return null;
        }

    }

    public static List<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> class1) {
        List<Method> methods = Lists.newArrayList();
        for (Method m : clazz.getMethods()) {
            if (m.isAnnotationPresent(class1)) methods.add(m);
        }

        return methods;
    }

}
