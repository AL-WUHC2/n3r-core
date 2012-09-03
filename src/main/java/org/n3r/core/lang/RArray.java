package org.n3r.core.lang;

public class RArray {
    public static <T> T first(T[] arr) {
        return arr != null && arr.length > 0 ? arr[0] : null;
    }
}
