package org.n3r.core.xml.utils;

import java.util.Map;

import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RClass;
import org.n3r.core.lang.RClassPath;
import org.n3r.core.xml.annotation.RXBindTo;
import org.n3r.core.xml.impl.BaseMarshaller;
import org.n3r.core.xml.impl.BaseUnmarshaller;

import static com.google.common.collect.Maps.*;
import static org.apache.commons.lang3.ClassUtils.*;

public class RXmlClassesScanner {

    private static Map<Class<?>, Class<?>> marshallerMap = newHashMap();
    private static Map<Class<?>, Class<?>> unmarshallerMap = newHashMap();

    static {
        for (Class<?> clz : RClassPath.getAnnotatedClasses("org.n3r.core.xml", RXBindTo.class)) {
            RXBindTo bindType = clz.getAnnotation(RXBindTo.class);

            Class<?>[] bindClasses = bindType.value();
            if (isAssignable(clz, BaseMarshaller.class)) {
                for (Class<?> bindClass : bindClasses) {
                    marshallerMap.put(bindClass, clz);
                }
            }
            if (isAssignable(clz, BaseUnmarshaller.class)) {
                for (Class<?> bindClass : bindClasses) {
                    unmarshallerMap.put(bindClass, clz);
                }
            }
        }
    }

    public static BaseMarshaller getMarshaller(Class<?> clazz) {
        return getRXBindClass(marshallerMap, clazz);
    }

    public static BaseUnmarshaller getUnmarshaller(Class<?> clazz) {
        return getRXBindClass(unmarshallerMap, clazz);
    }

    public static <T> T getRXBindClass(Map<Class<?>, Class<?>> map, Class<?> target) {
        Class<?> clz = map.get(target);
        if (clz == null && target.isPrimitive()) clz = map.get(primitiveToWrapper(target));
        if (clz == null) {
            for (Class<?> clazz : map.keySet()) {
                if (RClass.isAssignable(target, clazz)) clz = map.get(clazz);
            }
        }
        if (clz == null) return null;

        return Reflect.on(clz).create().get();
    }
}
