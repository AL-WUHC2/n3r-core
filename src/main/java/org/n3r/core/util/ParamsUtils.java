package org.n3r.core.util;

public class ParamsUtils {

    public static String getStr(String[] params, int index, String defaultValue) {
        return params != null && params.length > index ? params[index] : defaultValue;
    }

    public static int getInt(String[] params, int index, int defaultValue) {
        try {
            return params != null && params.length > index ? Integer.parseInt(params[index]) : defaultValue;
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

}
