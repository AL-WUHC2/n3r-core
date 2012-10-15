package org.n3r.eson.utils;

import java.security.PrivilegedAction;

import org.n3r.eson.EsonException;

public class ASMClassLoader extends ClassLoader {

    private static java.security.ProtectionDomain DOMAIN;

    static {
        DOMAIN = (java.security.ProtectionDomain) java.security.AccessController
                .doPrivileged(new PrivilegedAction<Object>() {

                    @Override
                    public Object run() {
                        return ASMClassLoader.class.getProtectionDomain();
                    }
                });
    }

    public ASMClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    public Class<?> defineClassPublic(String name, byte[] b, int off, int len) throws ClassFormatError {
        Class<?> clazz = defineClass(name, b, off, len, DOMAIN);

        return clazz;
    }

    public static Class<?> forName(String className) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new EsonException("class nout found : " + className);
        }
    }

    public static boolean isExternalClass(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();

        if (classLoader == null) return false;

        ClassLoader current = Thread.currentThread().getContextClassLoader();
        while (current != null) {
            if (current == classLoader) return false;

            current = current.getParent();
        }

        return true;
    }
}
