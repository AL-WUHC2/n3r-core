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

    /**
     * 获取一个类指定的属性（遍历父类）
     * @param clazz
     * @param fieldName
     * @return
     */
    public static Field getTraverseDeclaredField(Class<?> clazz, String fieldName) {
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            }
            catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
                continue;
            }
        }
        return field;
    }

}
