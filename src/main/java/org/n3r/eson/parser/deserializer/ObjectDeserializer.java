package org.n3r.eson.parser.deserializer;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.n3r.eson.EsonException;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.utils.ASMClassLoader;
import org.n3r.eson.utils.TypeUtils;

public class ObjectDeserializer implements EsonDeserializable {

    public final static ObjectDeserializer instance = new ObjectDeserializer();

    public Object parseMap(JSONParser parser, Map<Object, Object> map, Type keyType, Type valueType,
            Object fieldName) {
        JSONScanner lexer = parser.getLexer();

        if (lexer.token() != JSONToken.LBRACE && lexer.token() != JSONToken.COMMA)
            throw new EsonException("syntax error, expect {, actual " + lexer.tokenName());

        EsonDeserializable keyDeserializer = parser.getConfig().getDeserializer(keyType);
        EsonDeserializable valueDeserializer = parser.getConfig().getDeserializer(valueType);
        lexer.nextToken(keyDeserializer.getFastMatchToken());

        for (;;) {
            if (lexer.token() == JSONToken.RBRACE) {
                lexer.nextToken(JSONToken.COMMA);
                break;
            }

            if (map.size() == 0 && lexer.token() == JSONToken.LITERAL_STRING && "@type".equals(lexer.stringVal())) {
                lexer.nextTokenWithColon(JSONToken.LITERAL_STRING);
                lexer.nextToken(JSONToken.COMMA);
                lexer.nextToken(keyDeserializer.getFastMatchToken());
            }

            Object key = keyDeserializer.deserialize(parser, keyType, null);

            if (lexer.token() != JSONToken.COLON)
                throw new EsonException("syntax error, expect :, actual " + lexer.token());

            lexer.nextToken(valueDeserializer.getFastMatchToken());

            Object value = valueDeserializer.deserialize(parser, valueType, key);

            map.put(key, value);

            if (lexer.token() == JSONToken.COMMA) lexer.nextToken(keyDeserializer.getFastMatchToken());
        }

        return map;
    }

    @SuppressWarnings("rawtypes")
    public Map parseMap(JSONParser parser, Map<String, Object> map, Type valueType, Object fieldName) {
        JSONScanner lexer = parser.getLexer();

        if (lexer.token() != JSONToken.LBRACE)
            throw new EsonException("syntax error, expect {, actual " + lexer.token());

        for (;;) {
            lexer.skipWhitespace();
            char ch = lexer.getCurrent();
            while (ch == ',') {
                lexer.incrementBufferPosition();
                lexer.skipWhitespace();
                ch = lexer.getCurrent();
            }

            String key;
            if (ch == '"') {
                key = lexer.scanSymbol(parser.getSymbolTable(), '"');
                lexer.skipWhitespace();
                ch = lexer.getCurrent();
                if (ch != ':') throw new EsonException("expect ':' at " + lexer.pos());
            } else if (ch == '}') {
                lexer.incrementBufferPosition();
                lexer.resetStringPosition();
                lexer.nextToken(JSONToken.COMMA);
                return map;
            } else if (ch == '\'') {

                key = lexer.scanSymbol(parser.getSymbolTable(), '\'');
                lexer.skipWhitespace();
                ch = lexer.getCurrent();
                if (ch != ':') throw new EsonException("expect ':' at " + lexer.pos());
            } else {

                key = lexer.scanSymbolUnQuoted(parser.getSymbolTable());
                lexer.skipWhitespace();
                ch = lexer.getCurrent();
                if (ch != ':') throw new EsonException("expect ':' at " + lexer.pos() + ", actual " + ch);
            }

            lexer.incrementBufferPosition();
            lexer.skipWhitespace();
            ch = lexer.getCurrent();

            lexer.resetStringPosition();

            if (key == "@type") {
                String typeName = lexer.scanSymbol(parser.getSymbolTable(), '"');
                Class<?> clazz = TypeUtils.loadClass(typeName);

                if (clazz == map.getClass()) {
                    lexer.nextToken(JSONToken.COMMA);
                    if (lexer.token() == JSONToken.RBRACE) {
                        lexer.nextToken(JSONToken.COMMA);
                        return map;
                    }
                    continue;
                }

                EsonDeserializable deserializer = parser.getConfig().getDeserializer(clazz);

                lexer.nextToken(JSONToken.COMMA);

                parser.setResolveStatus(JSONParser.TypeNameRedirect);

                return (Map) deserializer.deserialize(parser, clazz, fieldName);
            }

            Object value;
            lexer.nextToken();

            if (lexer.token() == JSONToken.NULL) {
                value = null;
                lexer.nextToken();
            } else value = parser.parseObject(valueType);

            map.put(key, value);

            if (lexer.token() == JSONToken.RBRACE) {
                lexer.nextToken();
                return map;
            }
        }

    }

    public void parseObject(JSONParser parser, Object object) {
        Class<?> clazz = object.getClass();
        Map<String, FieldDeserializer> setters = parser.getConfig().getFieldDeserializers(clazz);

        JSONScanner lexer = parser.getLexer(); // xxx

        if (lexer.token() == JSONToken.RBRACE) {
            lexer.nextToken(JSONToken.COMMA);
            return;
        }

        if (lexer.token() != JSONToken.LBRACE && lexer.token() != JSONToken.COMMA)
            throw new EsonException("syntax error, expect {, actual " + lexer.tokenName());

        final Object[] args = new Object[1];

        for (;;) {
            // lexer.scanSymbol
            String key = lexer.scanSymbol(parser.getSymbolTable());

            if (key == null) {
                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                }
                if (lexer.token() == JSONToken.COMMA) continue;
            }

            FieldDeserializer fieldDeser = setters.get(key);
            if (fieldDeser == null) {

                lexer.nextTokenWithColon();
                parser.parse(); // skip

                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken();
                    return;
                }

                continue;
            } else {
                Method method = fieldDeser.getMethod();
                Class<?> fieldClass = method.getParameterTypes()[0];
                Type fieldType = method.getGenericParameterTypes()[0];
                if (fieldClass == int.class) {
                    lexer.nextTokenWithColon(JSONToken.LITERAL_INT);
                    args[0] = PrimitivesDeserializer.IntegerDeserializer.instance.deserialize(parser, null, null);
                } else if (fieldClass == String.class) {
                    lexer.nextTokenWithColon(JSONToken.LITERAL_STRING);
                    args[0] = StringDeserializer.instance.deserialize(parser, null, null);
                } else if (fieldClass == long.class) {
                    lexer.nextTokenWithColon(JSONToken.LITERAL_INT);
                    args[0] = PrimitivesDeserializer.LongDeserializer.instance.deserialize(parser, null, null);
                } else if (fieldClass == List.class) {
                    lexer.nextTokenWithColon(JSONToken.LBRACE);
                    args[0] = CollectionDeserializer.instance.deserialize(parser, fieldType, null);
                } else {
                    EsonDeserializable fieldValueDeserializer = parser.getConfig().getDeserializer(fieldClass,
                            fieldType);

                    lexer.nextTokenWithColon(fieldValueDeserializer.getFastMatchToken());
                    args[0] = fieldValueDeserializer.deserialize(parser, fieldType, null);
                }

                try {
                    method.invoke(object, args);
                } catch (Exception e) {
                    throw new EsonException("set proprety error, " + method.getName(), e);
                }
            }

            if (lexer.token() == JSONToken.COMMA) continue;

            if (lexer.token() == JSONToken.RBRACE) {
                lexer.nextToken(JSONToken.COMMA);
                return;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(JSONParser parser, Type type, Object fieldName) {
        if (type instanceof Class<?>) return deserialze(parser, (Class<?>) type);

        if (type instanceof ParameterizedType) return deserialze(parser, (ParameterizedType) type, fieldName);

        if (type instanceof TypeVariable) return parser.parse(fieldName);

        if (type instanceof WildcardType) return parser.parse(fieldName);

        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            List<Object> list = new ArrayList<Object>();
            parser.parseArray(componentType, list);
            if (componentType instanceof Class) {
                Class<?> componentClass = (Class<?>) componentType;
                Object[] array = (Object[]) Array.newInstance(componentClass, list.size());

                list.toArray(array);

                return array;
            }
        }

        throw new EsonException("not support type : " + type);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T> T deserialze(JSONParser parser, ParameterizedType type, Object fieldName) {
        try {
            JSONScanner lexer = parser.getLexer();
            if (lexer.token() == JSONToken.NULL) {
                lexer.nextToken();
                return null;
            }

            Type rawType = type.getRawType();
            if (rawType instanceof Class<?>) {
                Class<?> rawClass = (Class<?>) rawType;

                if (Map.class.isAssignableFrom(rawClass)) {
                    Map map;

                    if (Modifier.isAbstract(rawClass.getModifiers())) {
                        if (rawClass == Map.class) map = new HashMap();
                        else if (rawClass == SortedMap.class) map = new TreeMap();
                        else if (rawClass == ConcurrentMap.class) map = new ConcurrentHashMap();
                        else throw new EsonException("can not create instance : " + rawClass);
                    } else if (rawClass == HashMap.class) map = new HashMap();
                    else map = (Map) rawClass.newInstance();

                    Type keyType = type.getActualTypeArguments()[0];
                    Type valueType = type.getActualTypeArguments()[1];

                    if (keyType == String.class) return (T) parseMap(parser, map, valueType, fieldName);
                    else return (T) parseMap(parser, map, keyType, valueType, fieldName);
                }

            }

            throw new EsonException("not support type : " + type);
        } catch (EsonException e) {
            throw e;
        } catch (Throwable e) {
            throw new EsonException(e.getMessage(), e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object deserialze(JSONParser parser, Class<?> clazz) {
        Object value = null;

        if (parser.getLexer().token() == JSONToken.NULL) {
            parser.getLexer().nextToken(JSONToken.COMMA);
            return null;
        }
        if (clazz.isAssignableFrom(HashMap.class)) value = new HashMap();
        else if (clazz.isAssignableFrom(TreeMap.class)) value = new TreeMap();
        else if (clazz.isAssignableFrom(ConcurrentHashMap.class)) value = new ConcurrentHashMap();
        else if (clazz.isAssignableFrom(Properties.class)) value = new Properties();
        else if (clazz.isAssignableFrom(IdentityHashMap.class)) value = new IdentityHashMap();

        if (clazz == Class.class) {
            Object classValue = parser.parse();
            if (classValue == null) return null;

            if (classValue instanceof String) return ASMClassLoader.forName((String) classValue);
        } else if (clazz == Serializable.class) return parser.parse();

        if (value == null) throw new EsonException("not support type : " + clazz);

        try {
            parseObject(parser, value);
            return value;
        } catch (EsonException e) {
            throw e;
        } catch (Throwable e) {
            throw new EsonException(e.getMessage(), e);
        }
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
