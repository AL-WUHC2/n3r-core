package org.n3r.eson.parser;

import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.n3r.eson.EsonException;
import org.n3r.eson.utils.Base64;
import org.n3r.eson.utils.CharTypes;

import static org.n3r.eson.parser.JSONToken.*;

//这个类，为了性能优化做了很多特别处理，一切都是为了性能！！！

public class JSONScanner {
    public final static byte EOI = 0x1A;

    private final char[] buf;
    private int bp;
    private final int buflen;
    private int eofPos;

    // The current character.
    private char ch;
    //The token's position, 0-based offset from beginning of text.
    private int pos;
    // A character buffer for literals.
    private char[] sbuf;
    private int sp;
    // number start position
    private int np;
    //The token, set by nextToken().
    private int token;
    private Keywords keywods = Keywords.DEFAULT_KEYWORDS;
    private final static ThreadLocal<SoftReference<char[]>> sbufRefLocal = new ThreadLocal<SoftReference<char[]>>();
    private boolean resetFlag = false;

    private int mark;
    private char mark_ch;
    private int mark_token;
    private int mark_sp;

    public JSONScanner(String input) {
        this(input.toCharArray(), input.length());
    }

    public JSONScanner(char[] input, int inputLength) {
        SoftReference<char[]> sbufRef = sbufRefLocal.get();

        if (sbufRef != null) {
            sbuf = sbufRef.get();
            sbufRefLocal.set(null);
        }

        if (sbuf == null) sbuf = new char[64];

        eofPos = inputLength;

        if (inputLength == input.length) if (input.length > 0 && isWhitespace(input[input.length - 1])) inputLength--;
            else {
                char[] newInput = new char[inputLength + 1];
                System.arraycopy(input, 0, newInput, 0, input.length);
                input = newInput;
            }
        buf = input;
        buflen = inputLength;
        buf[buflen] = EOI;
        bp = -1;

        ch = buf[++bp];
    }

    public boolean isResetFlag() {
        return resetFlag;
    }

    public void setResetFlag(boolean resetFlag) {
        this.resetFlag = resetFlag;
    }

    public final int getBufferPosition() {
        return bp;
    }

    public void savePoint() {
        mark = bp;
        mark_ch = ch;
        mark_token = token;
        mark_sp = sp;
    }

    public void reset() {
        bp = mark;
        ch = mark_ch;
        token = mark_token;
        sp = mark_sp;

        resetFlag = true;
    }

    public boolean isBlankInput() {
        for (int i = 0; i < buflen; ++i)
            if (!isWhitespace(buf[i])) return false;

        return true;
    }

    public static final boolean isWhitespace(char ch) {
        // 专门调整了判断顺序
        return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b';
    }

    /**
     * Return the current token, set by nextToken().
     */
    public final int token() {
        return token;
    }

    public final String tokenName() {
        return JSONToken.name(token);
    }

    public final void skipWhitespace() {
        while (CharTypes.isWhitespace(ch))
            ch = buf[++bp];
    }

    public final char firstNoneWhiteSpace() {
        char ret = '\0';
        for (int i = bp; i < eofPos; ++i) {
            ret = buf[i];
            if (!CharTypes.isWhitespace(ret)) return ret;
        }
        return ret;
    }

    public final char firstNoneWhiteSpace(int bp) {
        char ret = buf[bp];
        for (int i = 0; i < eofPos; ++i) {
            ret = buf[bp + i];
            if (!CharTypes.isWhitespace(ret)) return ret;
        }
        return ret;
    }

    public final char getCurrent() {
        return ch;
    }

    public final void nextTokenWithColon() {
        for (;;) {
            if (ch == ':') {
                ch = buf[++bp];
                nextToken();
                return;
            }

            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b') {
                ch = buf[++bp];
                continue;
            }

            throw new EsonException("not match ':' - " + ch);
        }
    }

    public final void nextTokenWithColon(int expect) {
        for (;;) {
            if (ch == ':') {
                ch = buf[++bp];
                break;
            }

            if (isWhitespace(ch)) {
                ch = buf[++bp];
                continue;
            }

            throw new EsonException("not match ':', actual " + ch);
        }

        for (;;) {
            if (expect == JSONToken.LITERAL_INT) {
                if (ch >= '0' && ch <= '9') {
                    sp = 0;
                    pos = bp;
                    savePoint();
                    if (scanNumber()) return;
                    reset();
                    scanLenientString();
                    return;
                }

                if (ch == '"') {
                    sp = 0;
                    pos = bp;
                    scanString();
                    return;
                }
            } else if (expect == JSONToken.LITERAL_STRING) {
                if (ch == '"') {
                    sp = 0;
                    pos = bp;
                    scanString();
                    return;
                }

                if (ch >= '0' && ch <= '9') {
                    sp = 0;
                    pos = bp;
                    savePoint();
                    if (scanNumber()) return;
                    reset();
                    scanLenientString();
                    return;
                }

            } else if (expect == JSONToken.LBRACE) {
                if (ch == '{') {
                    token = JSONToken.LBRACE;
                    ch = buf[++bp];
                    return;
                }
                if (ch == '[') {
                    token = JSONToken.LBRACKET;
                    ch = buf[++bp];
                    return;
                }
            } else if (expect == JSONToken.LBRACKET) {
                if (ch == '[') {
                    token = JSONToken.LBRACKET;
                    ch = buf[++bp];
                    return;
                }

                if (ch == '{') {
                    token = JSONToken.LBRACE;
                    ch = buf[++bp];
                    return;
                }
            }

            if (isWhitespace(ch)) {
                ch = buf[++bp];
                continue;
            }

            nextToken();
            break;
        }
    }

    public final void incrementBufferPosition() {
        ch = buf[++bp];
    }

    public final void resetStringPosition() {
        sp = 0;
    }

    public void nextToken(int expect) {
        for (;;) {
            switch (expect) {
            case JSONToken.LBRACE:
                if (ch == '{') {
                    token = JSONToken.LBRACE;
                    ch = buf[++bp];
                    return;
                }
                if (ch == '[') {
                    token = JSONToken.LBRACKET;
                    ch = buf[++bp];
                    return;
                }
                break;
            case JSONToken.COMMA:
                if (ch == ',') {
                    token = JSONToken.COMMA;
                    ch = buf[++bp];
                    return;
                }

                if (ch == '}') {
                    token = JSONToken.RBRACE;
                    ch = buf[++bp];
                    return;
                }

                if (ch == ']') {
                    token = JSONToken.RBRACKET;
                    ch = buf[++bp];
                    return;
                }

                if (ch == EOI) {
                    token = JSONToken.EOF;
                    return;
                }
                break;
            case JSONToken.LITERAL_INT:
                if (ch >= '0' && ch <= '9') {
                    sp = 0;
                    pos = bp;
                    scanNumber();
                    return;
                }

                if (ch == '"') {
                    sp = 0;
                    pos = bp;
                    scanString();
                    return;
                }

                if (ch == '[') {
                    token = JSONToken.LBRACKET;
                    ch = buf[++bp];
                    return;
                }

                if (ch == '{') {
                    token = JSONToken.LBRACE;
                    ch = buf[++bp];
                    return;
                }

                break;
            case JSONToken.LITERAL_STRING:
                if (ch == '"') {
                    sp = 0;
                    pos = bp;
                    scanString();
                    return;
                }

                if (ch >= '0' && ch <= '9') {
                    sp = 0;
                    pos = bp;
                    scanNumber();
                    return;
                }

                if (ch == '[') {
                    token = JSONToken.LBRACKET;
                    ch = buf[++bp];
                    return;
                }

                if (ch == '{') {
                    token = JSONToken.LBRACE;
                    ch = buf[++bp];
                    return;
                }
                break;
            case JSONToken.LBRACKET:
                if (ch == '[') {
                    token = JSONToken.LBRACKET;
                    ch = buf[++bp];
                    return;
                }

                if (ch == '{') {
                    token = JSONToken.LBRACE;
                    ch = buf[++bp];
                    return;
                }
                break;
            case JSONToken.RBRACKET:
                if (ch == ']') {
                    token = JSONToken.RBRACKET;
                    ch = buf[++bp];
                    return;
                }
            case JSONToken.EOF:
                if (ch == EOI) {
                    token = JSONToken.EOF;
                    return;
                }
                break;
            default:
                break;
            }

            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b') {
                ch = buf[++bp];
                continue;
            }

            nextToken();
            break;
        }
    }

    public final void nextToken() {
        sp = 0;

        for (;;) {
            pos = bp;

            if (ch == '"') {
                scanString();
                return;
            }
            if (ch == '\'') {
                scanStringSingleQuote();
                return;
            }

            if (ch == ',') {
                ch = buf[++bp];
                token = COMMA;
                return;
            }

            if (ch >= '0' && ch <= '9') {
                if (!tryScanNumber()) scanLenientString();
                return;
            }

            if (ch == '-') {
                if (!tryScanNumber()) scanLenientString();
                return;
            }

            switch (ch) {
            case ' ':
            case '\t':
            case '\b':
            case '\f':
            case '\n':
            case '\r':
                ch = buf[++bp];
                break;
            case 't': // true
                if (!scanTrue()) scanLenientString();
                return;
            case 'T': // true
                if (!scanTreeSet()) scanLenientString();
                return;
            case 'S': // set
                if (!scanSet()) scanLenientString();
                return;
            case 'f': // false
                if (!scanFalse()) scanLenientString();
                return;
            case 'n': // new,null
                if (!scanNullOrNew()) scanLenientString();
                return;
                //            case 'D': // Date
                //                scanIdent();
                //                return;
            case '(':
                ch = buf[++bp];
                token = LPAREN;
                return;
            case ')':
                ch = buf[++bp];
                token = RPAREN;
                return;
            case '[':
                ch = buf[++bp];
                token = LBRACKET;
                return;
            case ']':
                ch = buf[++bp];
                token = RBRACKET;
                return;
            case '{':
                ch = buf[++bp];
                token = LBRACE;
                return;
            case '}':
                ch = buf[++bp];
                token = RBRACE;
                return;
            case ':':
                ch = buf[++bp];
                token = COLON;
                return;
            default:
                if (bp == buflen || ch == EOI && bp + 1 == buflen) { // JLS
                    if (token == EOF) throw new EsonException("EOF error");

                    token = EOF;
                    pos = bp = eofPos;
                } else scanLenientString();
                //                    lexError("illegal.char", String.valueOf((int) ch));
                //                    ch = buf[++bp];

                return;
            }
        }

    }

    boolean hasSpecial;

    public final void scanStringSingleQuote() {
        np = bp;
        hasSpecial = false;
        char ch;
        for (;;) {
            ch = buf[++bp];

            if (ch == '\'') break;

            if (ch == EOI) throw new EsonException("unclosed single-quote string");

            if (ch == '\\') {
                if (!hasSpecial) {
                    hasSpecial = true;

                    if (sp > sbuf.length) {
                        char[] newsbuf = new char[sp * 2];
                        System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
                        sbuf = newsbuf;
                    }

                    System.arraycopy(buf, np + 1, sbuf, 0, sp);
                }

                unescapeSpecialChar();
                continue;
            }

            if (!hasSpecial) {
                sp++;
                continue;
            }

            if (sp == sbuf.length) appendToSbuf(ch);
            else sbuf[sp++] = ch;
        }

        token = LITERAL_STRING;
        this.ch = buf[++bp];
    }

    public final void scanString() {
        np = bp;
        hasSpecial = false;
        char ch;
        for (;;) {
            ch = buf[++bp];

            if (ch == '\"') break;

            if (ch == '\\') {
                if (!hasSpecial) {
                    hasSpecial = true;

                    if (sp >= sbuf.length) {
                        int newCapcity = sbuf.length * 2;
                        if (sp > newCapcity) newCapcity = sp;
                        char[] newsbuf = new char[newCapcity];
                        System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
                        sbuf = newsbuf;
                    }

                    System.arraycopy(buf, np + 1, sbuf, 0, sp);
                }

                unescapeSpecialChar();
                continue;
            }

            if (!hasSpecial) {
                sp++;
                continue;
            }

            if (sp == sbuf.length) appendToSbuf(ch);
            else sbuf[sp++] = ch;
        }

        token = LITERAL_STRING;
        this.ch = buf[++bp];
    }

    public final void scanLenientString() {
        np = bp - 1;
        hasSpecial = false;
        sp++;
        char ch;
        for (; bp + 1 < eofPos;) {
            ch = buf[bp + 1];

            if (ch == ',' || ch == ']' || ch == '}') break;
            ++bp;
            if (CharTypes.isWhitespace(ch)) {
                char noneWhiteSpace = firstNoneWhiteSpace(bp);
                if (noneWhiteSpace == ',' || noneWhiteSpace == ']' || noneWhiteSpace == '}') break;
            }

            if (ch == '\"') break;

            if (ch == '\\') {
                if (!hasSpecial) {
                    hasSpecial = true;

                    if (sp >= sbuf.length) {
                        int newCapcity = sbuf.length * 2;
                        if (sp > newCapcity) newCapcity = sp;
                        char[] newsbuf = new char[newCapcity];
                        System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
                        sbuf = newsbuf;
                    }

                    System.arraycopy(buf, np + 1, sbuf, 0, sp);
                }

                unescapeSpecialChar();
                continue;
            }

            if (!hasSpecial) {
                sp++;
                continue;
            }

            if (sp == sbuf.length) appendToSbuf(ch);
            else sbuf[sp++] = ch;
        }

        token = LITERAL_STRING;
        this.ch = buf[++bp];
    }

    protected void unescapeSpecialChar() {
        char ch;
        ch = buf[++bp];

        char unescape = CharTypes.unescapeSpecialChars[ch];
        if (unescape != '\0') {
            appendToSbuf(unescape);
            return;
        }

        switch (ch) {
        case 'x':
            appendToSbuf((char) (digits[buf[++bp]] * 16 + digits[buf[++bp]]));
            break;
        case 'u':
            char[] chars = new char[] { buf[++bp], buf[++bp], buf[++bp], buf[++bp] };
            appendToSbuf((char) Integer.parseInt(new String(chars), 16));
            break;
        default:
            throw new EsonException("unclosed string : " + ch);
        }
    }

    public final String scanSymbolUnQuoted(final SymbolTable symbolTable) {
        final boolean[] firstIdentifierFlags = CharTypes.firstIdentifierFlags;
        final char first = ch;

        final boolean firstFlag = ch >= firstIdentifierFlags.length || firstIdentifierFlags[first];
        if (!firstFlag) throw new EsonException("illegal identifier : " + ch);

        final boolean[] identifierFlags = CharTypes.identifierFlags;

        int hash = first;

        np = bp;
        sp = 1;
        char ch;
        for (;;) {
            ch = buf[++bp];

            if (ch < identifierFlags.length) if (!identifierFlags[ch]) break;

            hash = 31 * hash + ch;

            sp++;
            continue;
        }

        this.ch = buf[bp];
        token = JSONToken.IDENTIFIER;

        final int NULL_HASH = 3392903;
        if (sp == 4 && hash == NULL_HASH && buf[np] == 'n' && buf[np + 1] == 'u' && buf[np + 2] == 'l'
                && buf[np + 3] == 'l') return null;

        return symbolTable.addSymbol(buf, np, sp, hash);
    }

    public final static int NOT_MATCH = -1;
    public final static int NOT_MATCH_NAME = -2;
    public final static int UNKOWN = 0;
    public final static int OBJECT = 1;
    public final static int ARRAY = 2;
    public final static int VALUE = 3;
    public final static int END = 4;

    private final static char[] typeFieldName = "\"@type\":\"".toCharArray();

    public int scanType(String type) {
        matchStat = UNKOWN;

        final int fieldNameLength = typeFieldName.length;

        for (int i = 0; i < fieldNameLength; ++i)
            if (typeFieldName[i] != buf[bp + i]) return NOT_MATCH_NAME;

        int bp = this.bp + fieldNameLength;

        final int typeLength = type.length();
        for (int i = 0; i < typeLength; ++i)
            if (type.charAt(i) != buf[bp + i]) return NOT_MATCH;
        bp += typeLength;
        if (buf[bp] != '"') return NOT_MATCH;

        ch = buf[++bp];

        if (ch == ',') {
            ch = buf[++bp];
            this.bp = bp;
            token = JSONToken.COMMA;
            return VALUE;
        } else if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else return NOT_MATCH;
            matchStat = END;
        }

        this.bp = bp;
        return matchStat;
    }

    public boolean matchField(char[] fieldName) {
        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) return false;

        bp = bp + fieldNameLength;
        ch = buf[bp];

        if (ch == '{') {
            ch = buf[++bp];
            token = JSONToken.LBRACE;
        } else if (ch == '[') {
            ch = buf[++bp];
            token = JSONToken.LBRACKET;
        } else nextToken();

        return true;
    }

    public int matchStat = UNKOWN;

    public String scanFieldString(char[] fieldName) {
        matchStat = UNKOWN;

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return null;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];
        if (ch != '"') {
            matchStat = NOT_MATCH;
            return null;
        }

        String strVal;
        int start = index;
        for (;;) {
            ch = buf[index++];
            if (ch == '\"') {
                bp = index;
                this.ch = ch = buf[bp];
                strVal = new String(buf, start, index - start - 1);
                break;
            }

            if (ch == '\\') {
                matchStat = NOT_MATCH;
                return null;
            }
        }

        if (ch == ',') {
            this.ch = buf[++bp];
            matchStat = VALUE;
            return strVal;
        } else if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else {
                matchStat = NOT_MATCH;
                return null;
            }
            matchStat = END;
        } else {
            matchStat = NOT_MATCH;
            return null;
        }

        return strVal;
    }

    public String scanFieldSymbol(char[] fieldName, final SymbolTable symbolTable) {
        matchStat = UNKOWN;

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return null;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];
        if (ch != '"') {
            matchStat = NOT_MATCH;
            return null;
        }

        String strVal;
        int start = index;
        int hash = 0;
        for (;;) {
            ch = buf[index++];
            if (ch == '\"') {
                bp = index;
                this.ch = ch = buf[bp];
                strVal = symbolTable.addSymbol(buf, start, index - start - 1, hash);
                break;
            }

            hash = 31 * hash + ch;

            if (ch == '\\') {
                matchStat = NOT_MATCH;
                return null;
            }
        }

        if (ch == ',') {
            this.ch = buf[++bp];
            matchStat = VALUE;
            return strVal;
        } else if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else {
                matchStat = NOT_MATCH;
                return null;
            }
            matchStat = END;
        } else {
            matchStat = NOT_MATCH;
            return null;
        }

        return strVal;
    }

    public ArrayList<String> scanFieldStringArray(char[] fieldName) {
        return (ArrayList<String>) scanFieldStringArray(fieldName, null);
    }

    @SuppressWarnings("unchecked")
    public Collection<String> scanFieldStringArray(char[] fieldName, Class<?> type) {
        matchStat = UNKOWN;

        Collection<String> list;

        if (type.isAssignableFrom(HashSet.class)) list = new HashSet<String>();
        else if (type.isAssignableFrom(ArrayList.class)) list = new ArrayList<String>();
        else try {
            list = (Collection<String>) type.newInstance();
        } catch (Exception e) {
            throw new EsonException(e.getMessage(), e);
        }

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return null;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];

        if (ch != '[') {
            matchStat = NOT_MATCH;
            return null;
        }

        ch = buf[index++];

        for (;;) {
            if (ch != '"') {
                matchStat = NOT_MATCH;
                return null;
            }

            String strVal;
            int start = index;
            for (;;) {
                ch = buf[index++];
                if (ch == '\"') {
                    strVal = new String(buf, start, index - start - 1);
                    list.add(strVal);
                    ch = buf[index++];
                    break;
                }

                if (ch == '\\') {
                    matchStat = NOT_MATCH;
                    return null;
                }
            }

            if (ch == ',') {
                ch = buf[index++];
                continue;
            }

            if (ch == ']') {
                ch = buf[index++];
                break;
            }

            matchStat = NOT_MATCH;
            return null;
        }

        bp = index;
        if (ch == ',') {
            this.ch = buf[bp];
            matchStat = VALUE;
            return list;
        } else if (ch == '}') {
            ch = buf[bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) {
                token = JSONToken.EOF;
                this.ch = ch;
            } else {
                matchStat = NOT_MATCH;
                return null;
            }
            matchStat = END;
        } else {
            matchStat = NOT_MATCH;
            return null;
        }

        return list;
    }

    public int scanFieldInt(char[] fieldName) {
        matchStat = UNKOWN;

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return 0;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];

        int value;
        if (ch >= '0' && ch <= '9') {
            value = digits[ch];
            for (;;) {
                ch = buf[index++];
                if (ch >= '0' && ch <= '9') value = value * 10 + digits[ch];
                else if (ch == '.') {
                    matchStat = NOT_MATCH;
                    return 0;
                } else {
                    bp = index - 1;
                    break;
                }
            }
            if (value < 0) {
                matchStat = NOT_MATCH;
                return 0;
            }
        } else {
            matchStat = NOT_MATCH;
            return 0;
        }

        if (ch == ',') {
            ch = buf[++bp];
            matchStat = VALUE;
            token = JSONToken.COMMA;
            return value;
        }

        if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else {
                matchStat = NOT_MATCH;
                return 0;
            }
            matchStat = END;
        }

        return value;
    }

    public boolean scanFieldBoolean(char[] fieldName) {
        matchStat = UNKOWN;

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return false;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];

        boolean value;
        if (ch == 't') {
            if (buf[index++] != 'r') {
                matchStat = NOT_MATCH;
                return false;
            }
            if (buf[index++] != 'u') {
                matchStat = NOT_MATCH;
                return false;
            }
            if (buf[index++] != 'e') {
                matchStat = NOT_MATCH;
                return false;
            }

            bp = index;
            ch = buf[bp];
            value = true;
        } else if (ch == 'f') {
            if (buf[index++] != 'a') {
                matchStat = NOT_MATCH;
                return false;
            }
            if (buf[index++] != 'l') {
                matchStat = NOT_MATCH;
                return false;
            }
            if (buf[index++] != 's') {
                matchStat = NOT_MATCH;
                return false;
            }
            if (buf[index++] != 'e') {
                matchStat = NOT_MATCH;
                return false;
            }

            bp = index;
            ch = buf[bp];
            value = false;
        } else {
            matchStat = NOT_MATCH;
            return false;
        }

        if (ch == ',') {
            ch = buf[++bp];
            matchStat = VALUE;
            token = JSONToken.COMMA;
        } else if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else {
                matchStat = NOT_MATCH;
                return false;
            }
            matchStat = END;
        } else {
            matchStat = NOT_MATCH;
            return false;
        }

        return value;
    }

    public long scanFieldLong(char[] fieldName) {
        matchStat = UNKOWN;

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return 0;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];

        long value;
        if (ch >= '0' && ch <= '9') {
            value = digits[ch];
            for (;;) {
                ch = buf[index++];
                if (ch >= '0' && ch <= '9') value = value * 10 + digits[ch];
                else if (ch == '.') {
                    token = NOT_MATCH;
                    return 0;
                } else {
                    bp = index - 1;
                    break;
                }
            }
            if (value < 0) {
                matchStat = NOT_MATCH;
                return 0;
            }
        } else {
            matchStat = NOT_MATCH;
            return 0;
        }

        if (ch == ',') {
            ch = buf[++bp];
            matchStat = VALUE;
            token = JSONToken.COMMA;
            return value;
        } else if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else {
                matchStat = NOT_MATCH;
                return 0;
            }
            matchStat = END;
        } else {
            matchStat = NOT_MATCH;
            return 0;
        }

        return value;
    }

    public float scanFieldFloat(char[] fieldName) {
        matchStat = UNKOWN;

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return 0;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];

        float value;
        if (ch >= '0' && ch <= '9') {
            int start = index - 1;
            for (;;) {
                ch = buf[index++];
                if (ch >= '0' && ch <= '9') continue;
                else break;
            }

            if (ch == '.') {
                ch = buf[index++];
                if (ch >= '0' && ch <= '9') for (;;) {
                    ch = buf[index++];
                    if (ch >= '0' && ch <= '9') continue;
                    else break;
                }
                else {
                    matchStat = NOT_MATCH;
                    return 0;
                }
            }

            bp = index - 1;
            String text = new String(buf, start, index - start - 1);
            value = Float.parseFloat(text);
        } else {
            matchStat = NOT_MATCH;
            return 0;
        }

        if (ch == ',') {
            ch = buf[++bp];
            matchStat = VALUE;
            token = JSONToken.COMMA;
            return value;
        } else if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else {
                matchStat = NOT_MATCH;
                return 0;
            }
            matchStat = END;
        } else {
            matchStat = NOT_MATCH;
            return 0;
        }

        return value;
    }

    public byte[] scanFieldByteArray(char[] fieldName) {
        matchStat = UNKOWN;

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return null;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];

        byte[] value;
        if (ch == '"' || ch == '\'') {
            char sep = ch;

            int startIndex = index;
            int endIndex = index;
            for (endIndex = index; endIndex < buf.length; ++endIndex)
                if (buf[endIndex] == sep) break;

            int base64Len = endIndex - startIndex;
            value = Base64.decodeFast(buf, startIndex, base64Len);
            if (value == null) {
                matchStat = NOT_MATCH;
                return null;
            }
            bp = endIndex + 1;
            ch = buf[bp];
        } else {
            matchStat = NOT_MATCH;
            return null;
        }

        if (ch == ',') {
            ch = buf[++bp];
            matchStat = VALUE;
            token = JSONToken.COMMA;
            return value;
        } else if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else {
                matchStat = NOT_MATCH;
                return null;
            }
            matchStat = END;
        } else {
            matchStat = NOT_MATCH;
            return null;
        }

        return value;
    }

    public byte[] bytesValue() {
        return Base64.decodeFast(buf, np + 1, sp);
    }

    public double scanFieldDouble(char[] fieldName) {
        matchStat = UNKOWN;

        final int fieldNameLength = fieldName.length;
        for (int i = 0; i < fieldNameLength; ++i)
            if (fieldName[i] != buf[bp + i]) {
                matchStat = NOT_MATCH_NAME;
                return 0;
            }

        int index = bp + fieldNameLength;

        char ch = buf[index++];

        double value;
        if (ch >= '0' && ch <= '9') {
            int start = index - 1;
            for (;;) {
                ch = buf[index++];
                if (ch >= '0' && ch <= '9') continue;
                else break;
            }

            if (ch == '.') {
                ch = buf[index++];
                if (ch >= '0' && ch <= '9') for (;;) {
                    ch = buf[index++];
                    if (ch >= '0' && ch <= '9') continue;
                    else break;
                }
                else {
                    matchStat = NOT_MATCH;
                    return 0;
                }
            }

            bp = index - 1;
            String text = new String(buf, start, index - start - 1);
            value = Double.parseDouble(text);
        } else {
            matchStat = NOT_MATCH;
            return 0;
        }

        if (ch == ',') {
            ch = buf[++bp];
            matchStat = VALUE;
            token = JSONToken.COMMA;
        } else if (ch == '}') {
            ch = buf[++bp];
            if (ch == ',') {
                token = JSONToken.COMMA;
                this.ch = buf[++bp];
            } else if (ch == ']') {
                token = JSONToken.RBRACKET;
                this.ch = buf[++bp];
            } else if (ch == '}') {
                token = JSONToken.RBRACE;
                this.ch = buf[++bp];
            } else if (ch == EOI) token = JSONToken.EOF;
            else {
                matchStat = NOT_MATCH;
                return 0;
            }
            matchStat = END;
        } else {
            matchStat = NOT_MATCH;
            return 0;
        }

        return value;
    }

    public String scanSymbol(final SymbolTable symbolTable) {
        skipWhitespace();

        if (ch == '"') return scanSymbol(symbolTable, '"');

        if (ch == '\'') return scanSymbol(symbolTable, '\'');

        if (ch == '}') {
            ch = buf[++bp];
            token = JSONToken.RBRACE;
            return null;
        }

        if (ch == ',') {
            ch = buf[++bp];
            token = JSONToken.COMMA;
            return null;
        }

        if (ch == EOI) {
            token = JSONToken.EOF;
            return null;
        }

        return scanSymbolUnQuoted(symbolTable);
    }

    public final String scanSymbol(final SymbolTable symbolTable, final char quote) {
        int hash = 0;

        np = bp;
        sp = 0;
        boolean hasSpecial = false;
        char ch;
        for (;;) {
            ch = buf[++bp];

            if (ch == quote) break;

            if (ch == EOI) throw new EsonException("unclosed.str");

            if (ch == '\\') {
                if (!hasSpecial) {
                    hasSpecial = true;

                    if (sp >= sbuf.length) {
                        int newCapcity = sbuf.length * 2;
                        if (sp > newCapcity) newCapcity = sp;
                        char[] newsbuf = new char[newCapcity];
                        System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
                        sbuf = newsbuf;
                    }

                    System.arraycopy(buf, np + 1, sbuf, 0, sp);
                }

                ch = buf[++bp];

                switch (ch) {
                case '"':
                    hash = 31 * hash + '"';
                    appendToSbuf('"');
                    break;
                case '\\':
                    hash = 31 * hash + '\\';
                    appendToSbuf('\\');
                    break;
                case '/':
                    hash = 31 * hash + '/';
                    appendToSbuf('/');
                    break;
                case 'b':
                    hash = 31 * hash + '\b';
                    appendToSbuf('\b');
                    break;
                case 'f':
                case 'F':
                    hash = 31 * hash + '\f';
                    appendToSbuf('\f');
                    break;
                case 'n':
                    hash = 31 * hash + '\n';
                    appendToSbuf('\n');
                    break;
                case 'r':
                    hash = 31 * hash + '\r';
                    appendToSbuf('\r');
                    break;
                case 't':
                    hash = 31 * hash + '\t';
                    appendToSbuf('\t');
                    break;
                case 'u':
                    char c1 = ch = buf[++bp];
                    char c2 = ch = buf[++bp];
                    char c3 = ch = buf[++bp];
                    char c4 = ch = buf[++bp];
                    int val = Integer.parseInt(new String(new char[] { c1, c2, c3, c4 }), 16);
                    hash = 31 * hash + val;
                    appendToSbuf((char) val);
                    break;
                default:
                    this.ch = ch;
                    throw new EsonException("unclosed.str.lit");
                }
                continue;
            }

            hash = 31 * hash + ch;

            if (!hasSpecial) {
                sp++;
                continue;
            }

            if (sp == sbuf.length) appendToSbuf(ch);
            else sbuf[sp++] = ch;
        }

        token = LITERAL_STRING;
        this.ch = buf[++bp];

        if (!hasSpecial) return symbolTable.addSymbol(buf, np + 1, sp, hash);
        else return symbolTable.addSymbol(sbuf, 0, sp, hash);
    }

    public boolean scanTrue() {
        savePoint();
        try {
            if (buf[bp++] != 't') throw new EsonException("error parse true");
            if (buf[bp++] != 'r') throw new EsonException("error parse true");
            if (buf[bp++] != 'u') throw new EsonException("error parse true");
            if (buf[bp++] != 'e') throw new EsonException("error parse true");

            ch = buf[bp];

            if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
                    || ch == '\f' || ch == '\b') token = JSONToken.TRUE;
            else throw new EsonException("scan true error");
        } catch (EsonException ex) {
            reset();
            return false;
        }
        return true;
    }

    public boolean scanSet() {
        savePoint();
        try {
            if (buf[bp++] != 'S') throw new EsonException("error parse true");
            if (buf[bp++] != 'e') throw new EsonException("error parse true");
            if (buf[bp++] != 't') throw new EsonException("error parse true");

            ch = buf[bp];

            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b' || ch == '['
                    || ch == '(') token = JSONToken.SET;
            else throw new EsonException("scan set error");
        } catch (EsonException ex) {
            reset();
            return false;
        }
        return true;
    }

    public boolean scanTreeSet() {
        savePoint();
        try {
            if (buf[bp++] != 'T') throw new EsonException("error parse true");
            if (buf[bp++] != 'r') throw new EsonException("error parse true");
            if (buf[bp++] != 'e') throw new EsonException("error parse true");
            if (buf[bp++] != 'e') throw new EsonException("error parse true");
            if (buf[bp++] != 'S') throw new EsonException("error parse true");
            if (buf[bp++] != 'e') throw new EsonException("error parse true");
            if (buf[bp++] != 't') throw new EsonException("error parse true");

            ch = buf[bp];

            if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t' || ch == '\f' || ch == '\b' || ch == '['
                    || ch == '(') token = JSONToken.TREE_SET;
            else throw new EsonException("scan set error");
        } catch (EsonException ex) {
            reset();
            return false;
        }
        return true;
    }

    public boolean scanNullOrNew() {
        savePoint();
        try {
            if (buf[bp++] != 'n') throw new EsonException("error parse null or new");

            if (buf[bp] == 'u') {
                bp++;
                if (buf[bp++] != 'l') throw new EsonException("error parse true");
                if (buf[bp++] != 'l') throw new EsonException("error parse true");
                ch = buf[bp];

                if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t'
                        || ch == EOI
                        || ch == '\f' || ch == '\b') token = JSONToken.NULL;
                else throw new EsonException("scan true error");
                return true;
            }

            if (buf[bp] != 'e') throw new EsonException("error parse e");

            bp++;
            if (buf[bp++] != 'w') throw new EsonException("error parse w");
            ch = buf[bp];

            if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
                    || ch == '\f' || ch == '\b') token = JSONToken.NEW;
            else throw new EsonException("scan true error");
        } catch (EsonException ex) {
            reset();
            return false;
        }
        return true;
    }

    public boolean scanFalse() {
        savePoint();
        try {
            if (buf[bp++] != 'f') throw new EsonException("error parse false");
            if (buf[bp++] != 'a') throw new EsonException("error parse false");
            if (buf[bp++] != 'l') throw new EsonException("error parse false");
            if (buf[bp++] != 's') throw new EsonException("error parse false");
            if (buf[bp++] != 'e') throw new EsonException("error parse false");

            ch = buf[bp];

            if (ch == ' ' || ch == ',' || ch == '}' || ch == ']' || ch == '\n' || ch == '\r' || ch == '\t' || ch == EOI
                    || ch == '\f' || ch == '\b') token = JSONToken.FALSE;
            else throw new EsonException("scan false error");
        } catch (EsonException ex) {
            reset();
            return false;
        }
        return true;
    }

    public void scanIdent() {
        np = bp - 1;
        hasSpecial = false;

        for (;;) {
            sp++;

            ch = buf[++bp];
            if (Character.isLetterOrDigit(ch)) continue;

            String ident = stringVal();

            Integer tok = keywods.getKeyword(ident);
            if (tok != null) token = tok;
            else token = JSONToken.IDENTIFIER;
            return;
        }
    }

    public boolean tryScanNumber() {
        savePoint();
        boolean ok = scanNumber();
        if (!ok) reset();

        return ok;
    }

    public boolean scanNumber() {
        np = bp;

        if (ch == '-') {
            sp++;
            ch = buf[++bp];
        }

        for (;;) {
            if (ch >= '0' && ch <= '9') sp++;
            else break;

            ch = buf[++bp];
            if (ch == EOI) break;
        }

        boolean isDouble = false;

        if (ch == '.') {
            sp++;
            ch = buf[++bp];
            isDouble = true;

            for (;;) {
                if (ch >= '0' && ch <= '9') sp++;
                else break;
                ch = buf[++bp];
            }
        }

        if (ch == 'L') {
            sp++;
            ch = buf[++bp];
        } else if (ch == 'S') {
            sp++;
            ch = buf[++bp];
        } else if (ch == 'B') {
            sp++;
            ch = buf[++bp];
        } else if (ch == 'F') {
            sp++;
            ch = buf[++bp];
            isDouble = true;
        } else if (ch == 'D') {
            sp++;
            ch = buf[++bp];
            isDouble = true;
        } else if (ch == 'e' || ch == 'E') {
            sp++;
            ch = buf[++bp];

            if (ch == '+' || ch == '-') {
                sp++;
                ch = buf[++bp];
            }

            for (;;) {
                if (ch >= '0' && ch <= '9') sp++;
                else break;
                ch = buf[++bp];
            }

            if (ch == 'D' || ch == 'F') ch = buf[++bp];

            isDouble = true;
        }

        if (isDouble) token = JSONToken.LITERAL_FLOAT;
        else token = JSONToken.LITERAL_INT;

        if (ch == EOI) return true;
        char firstNotEmpty = firstNoneWhiteSpace();

        return firstNotEmpty == '\0' || firstNotEmpty == ',' || firstNotEmpty == ']' || firstNotEmpty == '}';
    }

    private final void appendToSbuf(char ch) {
        if (sp == sbuf.length) {
            char[] newsbuf = new char[sbuf.length * 2];
            System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
            sbuf = newsbuf;
        }
        sbuf[sp++] = ch;
    }

    /**
     * Return the current token's position: a 0-based offset from beginning of the raw input stream (before unicode
     * translation)
     */
    public final int pos() {
        return pos;
    }

    /**
     * The value of a literal token, recorded as a string. For integers, leading 0x and 'l' suffixes are suppressed.
     */
    public final String stringVal() {
        if (!hasSpecial) return new String(buf, np + 1, sp);
        else return new String(sbuf, 0, sp);
    }

    //
    public boolean isRef() {
        if (hasSpecial) return false;

        if (sp != 4) return false;

        return buf[np + 1] == '$' && buf[np + 2] == 'r' && buf[np + 3] == 'e' && buf[np + 4] == 'f';
    }

    public final String symbol(SymbolTable symbolTable) {
        if (symbolTable == null) if (!hasSpecial) return new String(buf, np + 1, sp);
            else return new String(sbuf, 0, sp);

        if (!hasSpecial) return symbolTable.addSymbol(buf, np + 1, sp);
        else return symbolTable.addSymbol(sbuf, 0, sp);
    }

    private static final long MULTMIN_RADIX_TEN = Long.MIN_VALUE / 10;
    private static final long N_MULTMAX_RADIX_TEN = -Long.MAX_VALUE / 10;

    private static final int INT_MULTMIN_RADIX_TEN = Integer.MIN_VALUE / 10;
    private static final int INT_N_MULTMAX_RADIX_TEN = -Integer.MAX_VALUE / 10;

    private final static int[] digits = new int['f' + 1];

    static {
        for (int i = '0'; i <= '9'; ++i)
            digits[i] = i - '0';

        for (int i = 'a'; i <= 'f'; ++i)
            digits[i] = i - 'a' + 10;
        for (int i = 'A'; i <= 'F'; ++i)
            digits[i] = i - 'A' + 10;
    }

    public Number integerValue() throws NumberFormatException {
        long result = 0;
        boolean negative = false;
        int i = np, max = np + sp;
        long limit;
        long multmin;
        int digit;

        char type = ' ';

        if (max > 0) switch (buf[max - 1]) {
        case 'L':
            max--;
            type = 'L';
            break;
        case 'S':
            max--;
            type = 'S';
            break;
        case 'B':
            max--;
            type = 'B';
            break;
        default:
            break;
        }

        if (buf[np] == '-') {
            negative = true;
            limit = Long.MIN_VALUE;
            i++;
        } else limit = -Long.MAX_VALUE;
        multmin = negative ? MULTMIN_RADIX_TEN : N_MULTMAX_RADIX_TEN;
        if (i < max) {
            digit = digits[buf[i++]];
            result = -digit;
        }
        while (i < max) {
            // Accumulating negatively avoids surprises near MAX_VALUE
            digit = digits[buf[i++]];
            if (result < multmin) return new BigInteger(numberString());
            result *= 10;
            if (result < limit + digit) return new BigInteger(numberString());
            result -= digit;
        }

        if (negative) {
            if (i > np + 1) {
                if (result >= Integer.MIN_VALUE && type != 'L') {
                    if (type == 'S') return (short) result;

                    if (type == 'B') return (byte) result;

                    return (int) result;
                }
                return result;
            } else throw new NumberFormatException(numberString());
        } else {
            result = -result;
            if (result <= Integer.MAX_VALUE && type != 'L') {
                if (type == 'S') return (short) result;

                if (type == 'B') return (byte) result;

                return (int) result;
            }
            return result;
        }
    }

    public long longValue() throws NumberFormatException {
        long result = 0;
        boolean negative = false;
        int i = np, max = np + sp;
        long limit;
        long multmin;
        int digit;

        if (buf[np] == '-') {
            negative = true;
            limit = Long.MIN_VALUE;
            i++;
        } else limit = -Long.MAX_VALUE;
        multmin = negative ? MULTMIN_RADIX_TEN : N_MULTMAX_RADIX_TEN;
        if (i < max) {
            digit = digits[buf[i++]];
            result = -digit;
        }
        while (i < max) {
            // Accumulating negatively avoids surprises near MAX_VALUE
            char ch = buf[i++];

            if (ch == 'L' || ch == 'S' || ch == 'B') break;

            digit = digits[ch];
            if (result < multmin) throw new NumberFormatException(numberString());
            result *= 10;
            if (result < limit + digit) throw new NumberFormatException(numberString());
            result -= digit;
        }

        if (negative) {
            if (i > np + 1) return result;
            else throw new NumberFormatException(numberString());
        } else return -result;
    }

    public int intValue() {
        int result = 0;
        boolean negative = false;
        int i = np, max = np + sp;
        int limit;
        int multmin;
        int digit;

        if (buf[np] == '-') {
            negative = true;
            limit = Integer.MIN_VALUE;
            i++;
        } else limit = -Integer.MAX_VALUE;
        multmin = negative ? INT_MULTMIN_RADIX_TEN : INT_N_MULTMAX_RADIX_TEN;
        if (i < max) {
            digit = digits[buf[i++]];
            result = -digit;
        }
        while (i < max) {
            // Accumulating negatively avoids surprises near MAX_VALUE
            char ch = buf[i++];

            if (ch == 'L' || ch == 'S' || ch == 'B') break;

            digit = digits[ch];

            if (result < multmin) throw new NumberFormatException(numberString());
            result *= 10;
            if (result < limit + digit) throw new NumberFormatException(numberString());
            result -= digit;
        }

        if (negative) {
            if (i > np + 1) return result;
            else throw new NumberFormatException(numberString());
        } else return -result;
    }

    public final String numberString() {
        char ch = buf[np + sp - 1];

        int sp = this.sp;
        if (ch == 'L' || ch == 'S' || ch == 'B' || ch == 'F' || ch == 'D') sp--;

        return new String(buf, np, sp);
    }

    public float floatValue() {
        return Float.parseFloat(numberString());
    }

    public double doubleValue() {
        return Double.parseDouble(numberString());
    }

    public Number decimalValue(boolean decimal) {
        char ch = buf[np + sp - 1];
        if (ch == 'F') return Float.parseFloat(new String(buf, np, sp - 1));

        if (ch == 'D') return Double.parseDouble(new String(buf, np, sp - 1));

        if (decimal) return decimalValue();
        else return doubleValue();
    }

    public BigDecimal decimalValue() {
        char ch = buf[np + sp - 1];

        int sp = this.sp;
        if (ch == 'L' || ch == 'S' || ch == 'B' || ch == 'F' || ch == 'D') sp--;

        return new BigDecimal(buf, np, sp);
    }

    public boolean isEOF() {
        if (bp + 1 == eofPos) return true;

        switch (token) {
        case JSONToken.EOF:
            return true;
        case JSONToken.ERROR:
            return false;
        case JSONToken.RBRACE:
            return false;
        default:
            return false;
        }
    }

    public void close() {
        if (sbuf.length <= 1024 * 8) sbufRefLocal.set(new SoftReference<char[]>(sbuf));

        sbuf = null;
    }
}
