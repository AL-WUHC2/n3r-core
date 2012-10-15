package org.n3r.eson.utils;

public final class CharTypes {
    private static boolean[] whitespaceFlags = new boolean[256];
    static {
        whitespaceFlags[' '] = true;
        whitespaceFlags['\n'] = true;
        whitespaceFlags['\r'] = true;
        whitespaceFlags['\t'] = true;
        whitespaceFlags['\f'] = true;
        whitespaceFlags['\b'] = true;
    }

    public static boolean isWhitespace(char ch) {
        if (ch >= 0 && ch < 256) return whitespaceFlags[ch];

        return false;
    }

    public final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };

    public final static boolean[] firstIdentifierFlags = new boolean[256];
    static {
        for (char c = 0; c < firstIdentifierFlags.length; ++c)
            if (isLetterOrUnderscore(c)) firstIdentifierFlags[c] = true;
    }

    public final static boolean[] identifierFlags = new boolean[256];

    static {
        for (char c = 0; c < identifierFlags.length; ++c)
            if (isLetterOrUnderscore(c) || isDigit(c)) identifierFlags[c] = true;
    }

    protected static boolean isLetterOrUnderscore(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_';
    }

    protected static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public final static boolean[] specicalFlags = new boolean[128];

    public static boolean isSpecial(char ch) {
        return ch < specicalFlags.length && specicalFlags[ch];
    }

    private static enum ReplaceType {
        ESCAPE, UNESCAPE
    }

    public final static char[] escapeSpecialChars = new char[128];
    public final static char[] unescapeSpecialChars = new char[128];
    static {
        setSpecicalFlags(specicalFlags, '\"', '\\', /*'/',*/'\b', '\f', '\n', '\r', '\t', '\'', '\u000B');
        replaceChars(ReplaceType.ESCAPE, escapeSpecialChars, '\"', '\"'
                , '\"', '\"'
                // , '/', '/'
                , '\b', 'b'
                , '\f', 'f'
                , '\n', 'n'
                , '\r', 'r'
                , '\t', 't'
                , '\'', '\'',
                '\u000B', 'v');

        replaceChars(ReplaceType.UNESCAPE, unescapeSpecialChars, '\"', '\"'
                , '\"', '\"'
                //  , '/', '/'
                , '\b', 'b'
                , '\f', 'f'
                , '\n', 'n'
                , '\r', 'r'
                , '\t', 't'
                , '\'', '\'',
                '\u000B', 'v');
    }

    private static void setSpecicalFlags(boolean[] booleanArray, char... chars) {
        for (char ch : chars)
            booleanArray[ch] = true;
    }

    private static void replaceChars(ReplaceType type, char[] replaceChars, char... chars) {
        if (type == ReplaceType.ESCAPE) for (int i = 0, ii = chars.length; i < ii; i += 2)
            replaceChars[chars[i]] = chars[i + 1];
        else for (int i = 0, ii = chars.length; i < ii; i += 2)
            replaceChars[chars[i + 1]] = chars[i];
    }

}
