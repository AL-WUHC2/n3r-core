package org.n3r.core.xml.utils;

import java.util.Map;

import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RClassPath;
import org.n3r.core.xml.UnmarshalAware;
import org.n3r.core.xml.XMarshalAware;
import org.n3r.core.xml.annotation.RXBindTo;

import static com.google.common.collect.Maps.*;
import static org.apache.commons.lang3.ClassUtils.*;

public class RJaxbClassesScanner {

    private static Map<Class<?>, Class<?>> marshallerMap = newHashMap();
    private static Map<Class<?>, Class<?>> unmarshallerMap = newHashMap();

    static {
        for (Class<?> clz : RClassPath.getAnnotatedClasses("org.n3r.core.xml", RXBindTo.class)) {
            RXBindTo bindType = clz.getAnnotation(RXBindTo.class);

            Class<?>[] bindClasses = bindType.value();
            if (isAssignable(clz, XMarshalAware.class)) {
                for (Class<?> bindClass : bindClasses) {
                    marshallerMap.put(bindClass, clz);
                }
            }
            if (isAssignable(clz, UnmarshalAware.class)) {
                for (Class<?> bindClass : bindClasses) {
                    unmarshallerMap.put(bindClass, clz);
                }
            }
        }
    }

    public static XMarshalAware getMarshaller(Class<?> clazz) {
        return getRXBindClass(marshallerMap, clazz);
    }

    public static UnmarshalAware getUnmarshaller(Class<?> clazz) {
        return getRXBindClass(unmarshallerMap, clazz);
    }

    public static <T> T getRXBindClass(Map<Class<?>, Class<?>> map, Class<?> target) {
        Class<?> clz = map.get(target);
        if (clz == null && target.isPrimitive()) clz = map.get(primitiveToWrapper(target));
        if (clz == null) return null;

        return Reflect.on(clz).create().get();
    }

}
