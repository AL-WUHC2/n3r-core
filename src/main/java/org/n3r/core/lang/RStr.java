package org.n3r.core.lang;

import org.apache.commons.lang3.StringUtils;

public class RStr {
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean isEmpty(Object obj) {
        return isNull(obj) ? true : obj.toString().equals("");
    }

    public static boolean isNotEmpty(Object obj) {
        return isNotNull(obj) ? !obj.toString().equals("") : false;
    }

    public static String toString(Object obj) {
        return isNotEmpty(obj) ? obj.toString() : "";
    }

    public static String toString(Object obj, String defStr) {
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

}
