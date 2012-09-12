package org.n3r.core.lang;

import java.io.Closeable;
import java.lang.reflect.Method;

import com.google.common.io.Closeables;

public class RClose {

    public static void closeQuietly(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            Closeables.closeQuietly(closeable);
        }
    }

    public static void closeQuietly(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                continue;
            }

            if (obj instanceof Closeable) {
                Closeables.closeQuietly((Closeable) obj);
            }

            Method closeMethod = RMethod.getExactMethod(obj.getClass(), "close");
            if (closeMethod != null) {
                RMethod.invokeQuietly(closeMethod, obj);
            }
        }
    }

}
