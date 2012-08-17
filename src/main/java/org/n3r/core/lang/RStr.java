package org.n3r.core.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.n3r.core.lang3.StringUtils;

public class RStr {
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean isNotEmpty(Object obj) {
        return isNotNull(obj) ? !obj.toString().equals("") : false;
    }

    public static String toStr(Object obj) {
        return isNotEmpty(obj) ? obj.toString() : "";
    }

    public static String toStr(Object obj, String defStr) {
        return isNotEmpty(obj) ? obj.toString() : defStr;
    }

    public static StringBuilder removeTail(StringBuilder sb, String suffix) {
        if (!StringUtils.endsWith(sb, suffix)) return sb;

        return sb.replace(sb.length() - suffix.length(), sb.length(), "");
    }

    public static StringBuilder append(StringBuilder sb, char c) {
        return sb == null ? sb : sb.append(c);
    }

    public static StringBuilder append(StringBuilder sb, String str) {
        return sb == null ? sb : sb.append(str);
    }

    public static StringBuilder append(StringBuilder sb, Object str) {
        return sb == null ? sb : sb.append(str);
    }

    public static StringBuilder append(StringBuilder sb, boolean b) {
        return sb == null ? sb : sb.append(b);
    }

    public static StringBuilder append(StringBuilder sb, byte b) {
        return sb == null ? sb : sb.append(b);
    }

    public static StringBuilder append(StringBuilder sb, int b) {
        return sb == null ? sb : sb.append(b);
    }

    public static StringBuilder append(StringBuilder sb, long b) {
        return sb == null ? sb : sb.append(b);
    }

    public static StringBuilder append(StringBuilder sb, short b) {
        return sb == null ? sb : sb.append(b);
    }

    public static StringBuilder append(StringBuilder sb, double b) {
        return sb == null ? sb : sb.append(b);
    }

    public static StringBuilder append(StringBuilder sb, float b) {
        return sb == null ? sb : sb.append(b);
    }

    public static String repeat(String s, int count) {
        if (s == null || count < 1) return "";

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }

        return sb.toString();
    }

    public static String rpad(String s, int n) {
        return rpad(s, n, ' ');
    }

    public static String rpad(String s, int n, char padchar) {
        if (s == null) s = "";

        if (n > s.length()) {
            char[] ca = new char[n - s.length()];
            Arrays.fill(ca, padchar);
            return s.concat(String.valueOf(ca));
        }
        else {
            return s;
        }

    }

    public static String lpad(String s, int n) {
        return lpad(s, n, ' ');
    }

    public static String lpad(String s, int n, char padchar) {
        if (s == null) s = "";

        if (n > s.length()) {
            char[] ca = new char[n - s.length()];
            Arrays.fill(ca, padchar);
            return String.valueOf(ca).concat(s);
        }
        else {
            return s;
        }

    }

    public static boolean exists(String value, String valueList) {
        return exists(value, valueList, ",");
    }

    public static boolean exists(String value, String valueList, boolean ignoreCase) {
        return exists(value, valueList, ",", ignoreCase);
    }

    public static boolean exists(String value, String valueList, String sep) {
        return exists(value, valueList, sep, true);
    }

    public static boolean exists(String value, String valueList, String sep, boolean ignoreCase) {
        return exists(value.split(sep), valueList, ignoreCase);
    }

    public static boolean exists(String[] values, String valueList) {
        return exists(values, valueList, true);
    }

    public static boolean exists(String[] values, String valueList, boolean ignoreCase) {
        boolean ok = false;

        if (values != null && valueList != null) {
            final StringTokenizer st = new StringTokenizer(valueList, ",");

            while (!ok && st.hasMoreTokens()) {
                final String val1 = st.nextToken();
                if (val1 != null) {
                    for (int i = 0; i < values.length && !ok; i++) {
                        final String val2 = values[i];
                        if (val2 != null) {
                            if (ignoreCase) {
                                ok = val1.trim().equalsIgnoreCase(val2.trim());
                            }
                            else {
                                ok = val1.trim().equals(val2.trim());
                            }
                        }
                    }
                }
            }

        }

        return ok;

    }

    public static boolean isEmpty(Object value) {
        if (value == null) return true;

        if (value instanceof String) return StringUtils.isEmpty((String) value);

        return StringUtils.isEmpty(value.toString());
    }

    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
            boolean ignoreEmptyTokens) {
        final StringTokenizer st = new StringTokenizer(str, delimiters);
        final List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) { return null; }
        return collection.toArray(new String[collection.size()]);
    }

    public static String toUnix(Class<?> clazz) {
        return clazz.getName().replace(".", "/");
    }

    public static String toUnix(String path) {
        return path.replace("\\", "/");
    }

    public static String trim(String str, int length) {
        if (str == null) return null;

        String s = str.trim();

        if (s.length() > length) {
            s = s.substring(0, length);
        }

        return s;
    }

    public static String decode(String value, String... values) {
        String defaultValue = null;
        String retValue = null;

        if (values.length % 2 != 0) {
            defaultValue = values[values.length - 1];
            values = Arrays.copyOf(values, values.length - 1);
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                retValue = values[i + 1];
                break;
            }

            i++;

        }

        return (retValue == null ? defaultValue : retValue);

    }

    public static String removeQuotes(String value) {
        String s = null;
        if (value != null) {
            s = value.trim();
            if (s.startsWith("\"") && s.endsWith("\"")) {
                s = s.substring(1, s.length() - 1);
            }
        }

        return s;
    }
}
