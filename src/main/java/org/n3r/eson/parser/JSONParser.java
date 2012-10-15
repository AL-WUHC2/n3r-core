package org.n3r.eson.parser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.n3r.eson.EsonException;
import org.n3r.eson.JSONArray;
import org.n3r.eson.JSONObject;
import org.n3r.eson.parser.deserializer.ObjectDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer;
import org.n3r.eson.parser.deserializer.PrimitivesDeserializer.IntegerDeserializer;
import org.n3r.eson.parser.deserializer.StringDeserializer;
import org.n3r.eson.utils.TypeUtils;

import static org.n3r.eson.parser.JSONScanner.*;
import static org.n3r.eson.parser.JSONToken.*;

public class JSONParser {
    protected final Object input;
    protected final SymbolTable symbolTable;
    protected ParserMapping config;

    private ObjectDeserializer derializer = new ObjectDeserializer();

    private final static Set<Class<?>> primitiveClasses = new HashSet<Class<?>>();

    protected final JSONScanner lexer;

    public final static int NONE = 0;
    public final static int NeedToResolve = 1;
    public final static int TypeNameRedirect = 2;

    private int resolveStatus = NONE;

    static {
        primitiveClasses.add(boolean.class);
        primitiveClasses.add(byte.class);
        primitiveClasses.add(short.class);
        primitiveClasses.add(int.class);
        primitiveClasses.add(long.class);
        primitiveClasses.add(float.class);
        primitiveClasses.add(double.class);

        primitiveClasses.add(Boolean.class);
        primitiveClasses.add(Byte.class);
        primitiveClasses.add(Short.class);
        primitiveClasses.add(Integer.class);
        primitiveClasses.add(Long.class);
        primitiveClasses.add(Float.class);
        primitiveClasses.add(Double.class);

        primitiveClasses.add(BigInteger.class);
        primitiveClasses.add(BigDecimal.class);
        primitiveClasses.add(String.class);
    }

    public JSONParser(final Object input, final JSONScanner lexer, final ParserMapping config) {
        this.lexer = lexer;
        this.input = input;
        this.config = config;
        symbolTable = config.getSymbolTable();

        lexer.nextToken(JSONToken.LBRACE); // prime the pump
    }

    public JSONParser(String input, ParserMapping config) {
        this(input, new JSONScanner(input), config);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public String getInput() {
        if (input instanceof char[]) return new String((char[]) input);
        return input.toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final Object parseObject(final Map object, Object fieldName) {
        JSONScanner lexer = this.lexer;

        if (lexer.token() != JSONToken.LBRACE && lexer.token() != JSONToken.COMMA)
            throw new EsonException("syntax error, expect {, actual " + lexer.tokenName());

        for (;;) {
            lexer.skipWhitespace();
            char ch = lexer.getCurrent();
            while (ch == ',') {
                lexer.incrementBufferPosition();
                lexer.skipWhitespace();
                ch = lexer.getCurrent();
            }

            boolean isObjectKey = false;
            Object key;
            if (ch == '"') {
                key = lexer.scanSymbol(symbolTable, '"');
                lexer.skipWhitespace();
                ch = lexer.getCurrent();
                if (ch != ':') throw new EsonException("expect ':' at " + lexer.pos() + ", name " + key);
            } else if (ch == '}') {
                lexer.incrementBufferPosition();
                lexer.resetStringPosition();
                lexer.nextToken();
                return object;
            } else if (ch == '\'') {
                key = lexer.scanSymbol(symbolTable, '\'');
                lexer.skipWhitespace();
                ch = lexer.getCurrent();
                if (ch != ':') throw new EsonException("expect ':' at " + lexer.pos());
            } else if (ch == EOI) throw new EsonException("syntax error");
            else if (ch == ',') throw new EsonException("syntax error");
            else if (ch >= '0' && ch <= '9' || ch == '-') {
                lexer.resetStringPosition();
                lexer.scanNumber();
                if (lexer.token() == JSONToken.LITERAL_INT) key = lexer.integerValue();
                else key = lexer.decimalValue(true);
                ch = lexer.getCurrent();
                if (ch != ':') throw new EsonException("expect ':' at " + lexer.pos() + ", name " + key);
            } else if (ch == '{' || ch == '[') {
                lexer.nextToken();
                key = parse();
                isObjectKey = true;
            } else {
                key = lexer.scanSymbolUnQuoted(symbolTable);
                lexer.skipWhitespace();
                ch = lexer.getCurrent();
                if (ch != ':') throw new EsonException("expect ':' at " + lexer.pos() + ", actual " + ch);
            }

            if (!isObjectKey) {
                lexer.incrementBufferPosition();
                lexer.skipWhitespace();
            }

            ch = lexer.getCurrent();

            lexer.resetStringPosition();

            Object value;
            if (ch == '"') {
                lexer.scanString();
                String strValue = lexer.stringVal();
                value = strValue;

                if (object.getClass() == JSONObject.class) object.put(key.toString(), value);
                else object.put(key, value);
            } else if ((ch >= '0' && ch <= '9' || ch == '-') && lexer.tryScanNumber()) {
                if (lexer.token() == JSONToken.LITERAL_INT) value = lexer.integerValue();
                else value = lexer.decimalValue();

                object.put(key, value);
            } else if (ch == '[') { // 减少潜套，兼容android
                lexer.nextToken();
                JSONArray list = new JSONArray();
                this.parseArray(list, key);
                value = list;
                object.put(key, value);

                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken();
                    return object;
                } else if (lexer.token() == JSONToken.COMMA) continue;
                else throw new EsonException("syntax error");
            } else if (ch == '{') { // 减少潜套，兼容android
                lexer.nextToken();
                Object obj = this.parseObject(new JSONObject(), key);

                if (object.getClass() == JSONObject.class) object.put(key.toString(), obj);
                else object.put(key, obj);

                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken();

                    return object;
                } else if (lexer.token() == JSONToken.COMMA) continue;
                else throw new EsonException("syntax error, " + lexer.tokenName());
            } else {
                lexer.nextToken();
                value = parse();
                object.put(key, value);

                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken();
                    return object;
                } else if (lexer.token() == JSONToken.COMMA) continue;
                else throw new EsonException("syntax error, position at " + lexer.pos() + ", name " + key);
            }

            lexer.skipWhitespace();
            ch = lexer.getCurrent();
            if (ch == ',') {
                lexer.incrementBufferPosition();
                continue;
            } else if (ch == '}') {
                lexer.incrementBufferPosition();
                lexer.resetStringPosition();
                lexer.nextToken();

                return object;
            } else throw new EsonException("syntax error, position at " + lexer.pos() + ", name " + key);

        }

    }

    public ParserMapping getConfig() {
        return config;
    }

    public void setConfig(ParserMapping config) {
        this.config = config;
    }

    // compatible
    @SuppressWarnings("unchecked")
    public <T> T parseObject(Class<T> clazz) {
        return (T) parseObject((Type) clazz);
    }

    public Object parseObject(Type type) {
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            return null;
        }

        EsonDeserializable derializer = config.getDeserializer(type);

        try {
            return derializer.deserialize(this, type, null);
        } catch (EsonException e) {
            throw e;
        } catch (Throwable e) {
            throw new EsonException(e.getMessage(), e);
        }
    }

    public <T> List<T> parseArray(Class<T> clazz) {
        List<T> array = new ArrayList<T>();
        parseArray(clazz, array);
        return array;
    }

    public void parseArray(Class<?> clazz, @SuppressWarnings("rawtypes") Collection array) {
        parseArray((Type) clazz, array);
    }

    @SuppressWarnings("rawtypes")
    public void parseArray(Type type, Collection array) {
        parseArray(type, array, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void parseArray(Type type, Collection array, Object fieldName) {
        if (lexer.token() == JSONToken.SET || lexer.token() == JSONToken.TREE_SET) lexer.nextToken();

        if (lexer.token() != JSONToken.LBRACKET)
            throw new EsonException("exepct '[', but " + JSONToken.name(lexer.token()));

        EsonDeserializable deserializer = null;
        if (int.class == type) {
            deserializer = PrimitivesDeserializer.IntegerDeserializer.instance;
            lexer.nextToken(JSONToken.LITERAL_INT);
        } else if (String.class == type) {
            deserializer = StringDeserializer.instance;
            lexer.nextToken(JSONToken.LITERAL_STRING);
        } else {
            deserializer = config.getDeserializer(type);
            lexer.nextToken(deserializer.getFastMatchToken());
        }

        for (int i = 0;; ++i) {
            while (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken();
                continue;
            }

            if (lexer.token() == JSONToken.RBRACKET) break;

            if (int.class == type) {
                Object val = IntegerDeserializer.instance.deserialize(this, null, null);
                array.add(val);
            } else if (String.class == type) {
                String value;
                if (lexer.token() == JSONToken.LITERAL_STRING) {
                    value = lexer.stringVal();
                    lexer.nextToken(JSONToken.COMMA);
                } else {
                    Object obj = this.parse();
                    if (obj == null) value = null;
                    else value = obj.toString();
                }

                array.add(value);
            } else {
                Object val;
                if (lexer.token() == JSONToken.NULL) {
                    lexer.nextToken();
                    val = null;
                } else val = deserializer.deserialize(this, type, i);
                array.add(val);
            }

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(deserializer.getFastMatchToken());
                continue;
            }
        }

        lexer.nextToken(JSONToken.COMMA);
    }

    public Object[] parseArray(Type[] types) {
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            return null;
        }

        if (lexer.token() != JSONToken.LBRACKET) throw new EsonException("syntax error : " + lexer.tokenName());

        Object[] list = new Object[types.length];
        if (types.length == 0) {
            lexer.nextToken(JSONToken.RBRACKET);

            if (lexer.token() != JSONToken.RBRACKET) throw new EsonException("syntax error");

            lexer.nextToken(JSONToken.COMMA);
            return new Object[0];
        }

        lexer.nextToken(JSONToken.LITERAL_INT);

        for (int i = 0; i < types.length; ++i) {
            Object value;

            if (lexer.token() == JSONToken.NULL) {
                value = null;
                lexer.nextToken(JSONToken.COMMA);
            } else {
                Type type = types[i];
                if (type == int.class || type == Integer.class) {
                    if (lexer.token() == JSONToken.LITERAL_INT) {
                        value = Integer.valueOf(lexer.intValue());
                        lexer.nextToken(JSONToken.COMMA);
                    } else {
                        value = this.parse();
                        value = TypeUtils.cast(value, type, config);
                    }
                } else if (type == String.class) {
                    if (lexer.token() == JSONToken.LITERAL_STRING) {
                        value = lexer.stringVal();
                        lexer.nextToken(JSONToken.COMMA);
                    } else {
                        value = this.parse();
                        value = TypeUtils.cast(value, type, config);
                    }
                } else {
                    boolean isArray = false;
                    Class<?> componentType = null;
                    if (i == types.length - 1) if (type instanceof Class) {
                        Class<?> clazz = (Class<?>) type;
                        isArray = clazz.isArray();
                        componentType = clazz.getComponentType();
                    }

                    // support varArgs
                    if (isArray && lexer.token() != JSONToken.LBRACKET) {
                        List<Object> varList = new ArrayList<Object>();

                        EsonDeserializable derializer = config.getDeserializer(componentType);
                        int fastMatch = derializer.getFastMatchToken();

                        if (lexer.token() != JSONToken.RBRACKET) for (;;) {
                            Object item = derializer.deserialize(this, type, null);
                            varList.add(item);

                            if (lexer.token() == JSONToken.COMMA) lexer.nextToken(fastMatch);
                            else if (lexer.token() == JSONToken.RBRACKET) break;
                            else throw new EsonException("syntax error :" + JSONToken.name(lexer.token()));
                        }

                        value = TypeUtils.cast(varList, type, config);
                    } else {
                        EsonDeserializable derializer = config.getDeserializer(type);
                        value = derializer.deserialize(this, type, null);
                    }
                }
            }
            list[i] = value;

            if (lexer.token() == JSONToken.RBRACKET) break;

            if (lexer.token() != JSONToken.COMMA)
                throw new EsonException("syntax error :" + JSONToken.name(lexer.token()));

            if (i == types.length - 1) lexer.nextToken(JSONToken.RBRACKET);
            else lexer.nextToken(JSONToken.LITERAL_INT);
        }

        if (lexer.token() != JSONToken.RBRACKET) throw new EsonException("syntax error");

        lexer.nextToken(JSONToken.COMMA);

        return list;
    }

    public void parseObject(Object object) {
        derializer.parseObject(this, object);
    }

    public Object parseArrayWithType(Type collectionType) {
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            return null;
        }

        Type[] actualTypes = ((ParameterizedType) collectionType).getActualTypeArguments();

        if (actualTypes.length != 1) throw new EsonException("not support type " + collectionType);

        Type actualTypeArgument = actualTypes[0];

        if (actualTypeArgument instanceof Class) {
            List<Object> array = new ArrayList<Object>();
            this.parseArray((Class<?>) actualTypeArgument, array);
            return array;
        }

        if (actualTypeArgument instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) actualTypeArgument;

            // assert wildcardType.getUpperBounds().length == 1;
            Type upperBoundType = wildcardType.getUpperBounds()[0];

            // assert upperBoundType instanceof Class;
            if (Object.class.equals(upperBoundType)) if (wildcardType.getLowerBounds().length == 0) // Collection<?>
                return parse();
                else throw new EsonException("not support type : " + collectionType);

            List<Object> array = new ArrayList<Object>();
            this.parseArray((Class<?>) upperBoundType, array);
            return array;

            // throw new EsonException("not support type : " +
            // collectionType);return parse();
        }

        if (actualTypeArgument instanceof TypeVariable) {
            TypeVariable<?> typeVariable = (TypeVariable<?>) actualTypeArgument;
            Type[] bounds = typeVariable.getBounds();

            if (bounds.length != 1) throw new EsonException("not support : " + typeVariable);

            Type boundType = bounds[0];
            if (boundType instanceof Class) {
                List<Object> array = new ArrayList<Object>();
                this.parseArray((Class<?>) boundType, array);
                return array;
            }
        }

        if (actualTypeArgument instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) actualTypeArgument;

            List<Object> array = new ArrayList<Object>();
            this.parseArray(parameterizedType, array);
            return array;
        }

        throw new EsonException("TODO : " + collectionType);
    }

    public void acceptType(String typeName) {
        JSONScanner lexer = this.lexer;

        lexer.nextTokenWithColon();

        if (lexer.token() != JSONToken.LITERAL_STRING) throw new EsonException("type not match error");

        if (typeName.equals(lexer.stringVal())) {
            lexer.nextToken();
            if (lexer.token() == JSONToken.COMMA) lexer.nextToken();
        } else throw new EsonException("type not match error");
    }

    public int getResolveStatus() {
        return resolveStatus;
    }

    public void setResolveStatus(int resolveStatus) {
        this.resolveStatus = resolveStatus;
    }

    @SuppressWarnings("rawtypes")
    public Object parseObject(final Map object) {
        return parseObject(object, null);
    }

    public JSONObject parseObject() {
        JSONObject object = new JSONObject();
        parseObject(object);
        return object;
    }

    @SuppressWarnings("rawtypes")
    public final void parseArray(final Collection array) {
        parseArray(array, null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void parseArray(final Collection array, Object fieldName) {
        final JSONScanner lexer = getLexer();

        if (lexer.token() == JSONToken.SET || lexer.token() == JSONToken.TREE_SET) lexer.nextToken();

        if (lexer.token() != JSONToken.LBRACKET)
            throw new EsonException("syntax error, expect [, actual " + JSONToken.name(lexer.token()));

        lexer.nextToken(JSONToken.LITERAL_STRING);

        for (int i = 0;; ++i) {
            while (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken();
                continue;
            }

            Object value;
            switch (lexer.token()) {
            case LITERAL_INT:
                value = lexer.integerValue();
                lexer.nextToken(JSONToken.COMMA);
                break;
            case LITERAL_FLOAT:
                value = lexer.decimalValue(false);
                lexer.nextToken(JSONToken.COMMA);
                break;
            case LITERAL_STRING:
                value = lexer.stringVal();
                lexer.nextToken(JSONToken.COMMA);
                break;
            case TRUE:
                value = Boolean.TRUE;
                lexer.nextToken(JSONToken.COMMA);
                break;
            case FALSE:
                value = Boolean.FALSE;
                lexer.nextToken(JSONToken.COMMA);
                break;
            case LBRACE:
                JSONObject object = new JSONObject();
                value = parseObject(object, i);
                break;
            case LBRACKET:
                Collection items = new JSONArray();
                parseArray(items, i);
                value = items;
                break;
            case NULL:
                value = null;
                lexer.nextToken(JSONToken.LITERAL_STRING);
                break;
            case RBRACKET:
                lexer.nextToken(JSONToken.COMMA);
                return;
            default:
                value = parse();
                break;
            }

            array.add(value);

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(JSONToken.LITERAL_STRING);
                continue;
            }
        }
    }

    public Object parse() {
        return parse(null);
    }

    public Object parse(Object fieldName) {
        final JSONScanner lexer = getLexer();
        switch (lexer.token()) {
        case SET:
            lexer.nextToken();
            HashSet<Object> set = new HashSet<Object>();
            parseArray(set, fieldName);
            return set;
        case TREE_SET:
            lexer.nextToken();
            TreeSet<Object> treeSet = new TreeSet<Object>();
            parseArray(treeSet, fieldName);
            return treeSet;
        case LBRACKET:
            JSONArray array = new JSONArray();
            parseArray(array, fieldName);
            return array;
        case LBRACE:
            JSONObject object = new JSONObject();
            return parseObject(object, fieldName);
        case LITERAL_INT:
            Number intValue = lexer.integerValue();
            lexer.nextToken();
            return intValue;
        case LITERAL_FLOAT:
            Object value = lexer.decimalValue(false);
            lexer.nextToken();
            return value;
        case LITERAL_STRING:
            String stringLiteral = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);

            return stringLiteral;
        case NULL:
            lexer.nextToken();
            return null;
        case TRUE:
            lexer.nextToken();
            return Boolean.TRUE;
        case FALSE:
            lexer.nextToken();
            return Boolean.FALSE;
        case NEW:
            lexer.nextToken(JSONToken.IDENTIFIER);

            if (lexer.token() != JSONToken.IDENTIFIER) throw new EsonException("syntax error");
            lexer.nextToken(JSONToken.LPAREN);

            accept(JSONToken.LPAREN);
            long time = lexer.integerValue().longValue();
            accept(JSONToken.LITERAL_INT);

            accept(JSONToken.RPAREN);

            return new Date(time);
        case EOF:
            if (lexer.isBlankInput()) return null;
            throw new EsonException("unterminated json string, pos " + lexer.getBufferPosition());
        case ERROR:
        default:
            throw new EsonException("syntax error, pos " + lexer.getBufferPosition());
        }
    }

    public JSONScanner getLexer() {
        return lexer;
    }

    public final void accept(final int token) {
        final JSONScanner lexer = getLexer();
        if (lexer.token() == token) lexer.nextToken();
        else throw new EsonException("syntax error, expect " + JSONToken.name(token) + ", actual "
                + JSONToken.name(lexer.token()));
    }

    public void close() {
        final JSONScanner lexer = getLexer();

        try {
            if (!lexer.isEOF())
                throw new EsonException("not close json text, token : " + JSONToken.name(lexer.token()));
        } finally {
            lexer.close();
        }
    }

}
