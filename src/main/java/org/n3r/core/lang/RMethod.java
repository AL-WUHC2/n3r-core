package org.n3r.core.lang;

import java.lang.reflect.Method;

import com.google.common.base.Throwables;

public class RMethod {

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Method method, Object object, Object[] args) {
        try {
            return (T) method.invoke(object, args);
        }
        catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

}
