package org.n3r.eson.parser;

public class JSONToken {
    public final static int ERROR = 1;
    public final static int LITERAL_INT = 2;
    public final static int LITERAL_FLOAT = 3;
    public final static int LITERAL_STRING = 4;
    public final static int LITERAL_ISO8601_DATE = 5;
    public final static int TRUE = 6;
    public final static int FALSE = 7;
    public final static int NULL = 8;
    public final static int NEW = 9;
    public final static int LPAREN = 10; // ("("),
    public final static int RPAREN = 11; // (")"),
    public final static int LBRACE = 12; // ("{"),
    public final static int RBRACE = 13; // ("}"),
    public final static int LBRACKET = 14; // ("["),
    public final static int RBRACKET = 15; // ("]"),
    public final static int COMMA = 16; // (","),
    public final static int COLON = 17; // (":"),
    public final static int IDENTIFIER = 18;
    public final static int FIELD_NAME = 19;
    public final static int EOF = 20;
    public final static int SET = 21;
    public final static int TREE_SET = 22;

    private static String[] TOKEN_NAME = new String[] { "", "error", "int", "float", "string", "iso8601",
            "true", "false", "null", "new", "(", ")", "{", "}", "[", "]", ",", ":", "ident", "fieldName", "EOF", "Set",
            "TreeSet" };

    public static String name(int value) {
        return value >= 0 && value < TOKEN_NAME.length ? TOKEN_NAME[value] : "Unkown";
    }
}
