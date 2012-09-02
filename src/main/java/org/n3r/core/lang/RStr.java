package org.n3r.core.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class RStr {
    public static String substrInQuotes(String str, char left, int pos) {
        int leftTimes = 0;
        int leftPos = str.indexOf(left, pos);
        if (leftPos < 0) return "";

        for (int i = leftPos + 1; i < str.length(); ++i) {
            char charAt = str.charAt(i);
            if (charAt == left) ++leftTimes;
            else if (matches(left, charAt)) {
                if (leftTimes == 0) return str.substring(leftPos + 1, i);
                --leftTimes;
            }
        }

        return "";
    }
    // return true if 'left' and 'right' are matching parens/brackets/braces
    public static boolean matches(char left, char right) {
        if (left == '(') return right == ')';
        if (left == '[') return right == ']';
        if (left == '{') return right == '}';
        return false;
    }

    /**
     * 确定一个对象是不是在后续的可变参数列表中。
     * @param target 目标对象。
     * @param compares 比较对象。
     * @return true在列表中
     */
    public static <T> boolean in(T target, T... compares) {
        for (T compare : compares)
            if (ObjectUtils.equals(target, compare)) return true;

        return false;
    }

    /**
     * 保证字符串为一固定长度。超过长度，切除，否则补字符。
     *
     * @param s
     *            字符串
     * @param width
     *            长度
     * @param c
     *            补字符
     * @return 修饰后的字符串
     */
    public static String cutRight(String s, int width, char c) {
        if (null == s) return StringUtils.repeat(c, width);
        int len = s.length();
        if (len == width) return s;
        if (len < width) return StringUtils.repeat(c, width - len) + s;
        return s.substring(len - width, len);
    }

    /**
     * 在字符串左侧填充一定数量的特殊字符.
     *
     * @param cs
     *            字符串
     * @param width
     *            字符数量
     * @param c
     *            字符
     * @return 新字符串
     */
    public static String alignRight(CharSequence cs, int width, char c) {
        if (null == cs) return null;
        int len = cs.length();
        if (len >= width) return cs.toString();
        return StringUtils.repeat(c, width - len) + cs;
    }

    /**
     * 在字符串左侧侧填充一定数量的特殊字符.
     *
     * @param cs
     *            字符串
     * @param width
     *            字符数量
     * @param c
     *            字符
     * @return 新字符串
     */
    public static String alignLeft(CharSequence cs, int width, char c) {
        if (null == cs) return null;
        int length = cs.length();
        if (length >= width) return cs.toString();
        return cs + StringUtils.repeat(c, width - length);
    }

    /**
     * Convert a name in camelCase to an underscored name in lower case.
     * Any upper case letters are converted to lower case with a preceding underscore.
     * @param name the string containing original name
     * @return the converted name
     */
    public static String underscore(String name) {
        StringBuilder result = new StringBuilder();
        if (name != null && name.length() > 0) {
            result.append(name.substring(0, 1).toLowerCase());
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if (s.equals(s.toUpperCase())) {
                    result.append("_");
                    result.append(s.toLowerCase());
                }
                else result.append(s);
            }
        }

        return result.toString();
    }

    public static String convertUnderscoreNameToPropertyName(String name) {
        StringBuilder result = new StringBuilder();
        boolean nextIsUpper = false;
        if (name != null && name.length() > 0) {
            if (name.length() > 1 && name.substring(1, 2).equals("_")) {
                result.append(name.substring(0, 1).toUpperCase());
            }
            else {
                result.append(name.substring(0, 1).toLowerCase());
            }
            for (int i = 1; i < name.length(); i++) {
                String s = name.substring(i, i + 1);
                if (s.equals("_")) {
                    nextIsUpper = true;
                }
                else {
                    if (nextIsUpper) {
                        result.append(s.toUpperCase());
                        nextIsUpper = false;
                    }
                    else {
                        result.append(s.toLowerCase());
                    }
                }
            }
        }
        return result.toString();
    }

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
        for (int i = 0; i < count; i++)
            sb.append(s);

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
        else return s;

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
        else return s;

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
        if (collection == null) return null;
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

        if (s.length() > length) s = s.substring(0, length);

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

        return retValue == null ? defaultValue : retValue;

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

    public static int indexOfBlank(CharSequence cs) {
        int sz = cs.length();
        for (int i = 0; i < sz; i++)
            if (Character.isWhitespace(cs.charAt(i))) return i;

        return -1;
    }

    public static String substringBeforeFirstBlank(String cs) {
        int indexOfBlank = indexOfBlank(cs);
        if (indexOfBlank < 0) return cs;

        return cs.substring(0, indexOfBlank);
    }

    public static String trimRight(String original) {
        return original.replaceAll("\\s+$", "");
    }

    public static boolean isQuoted(String str, String leftQuote, String rightQuote) {
        return StringUtils.startsWith(str, leftQuote) && StringUtils.endsWith(str, rightQuote);
    }

    public static StringBuilder clear(StringBuilder sql) {
        return sql.replace(0, sql.length(), "");
    }
}
