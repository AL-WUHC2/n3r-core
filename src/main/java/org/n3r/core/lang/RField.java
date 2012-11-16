package org.n3r.core.lang;

import java.lang.reflect.Field;

public class RField {

    public static boolean isNotNormal(Field field) {
        return field.getName().startsWith("$") || field.getName().equals("serialVersionUID");
    }

    public static Field getDeclaredField(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (Exception e) {
            // Ignore
            return null;
        }
    }

}
