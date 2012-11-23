package org.n3r.core.lang;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class RType {
    /**
     * Returns actual type argument of a generic type.
     * @param genericType
     * @return Void.class if it's N/A.
     */
    public static Class<?> getActualTypeArgument(Type genericType) {
        Class<? extends Type> genericTypeClass = genericType.getClass();
        if (!ParameterizedType.class.isAssignableFrom(genericTypeClass)) return Void.class;

        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        try {
            return (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }
        catch (ClassCastException ex) {
            return Void.class;
        }
    }

    public static boolean isTypeVariable(Type genericType) {
        return TypeVariable.class.isAssignableFrom(genericType.getClass());
    }
}
