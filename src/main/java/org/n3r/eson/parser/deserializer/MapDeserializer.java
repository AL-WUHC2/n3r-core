package org.n3r.eson.parser.deserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.n3r.eson.EsonException;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.utils.AntiCollisionHashMap;

public class MapDeserializer implements EsonDeserializable {

    public final static MapDeserializer instance = new MapDeserializer();

    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(JSONParser parser, Type type, Object fieldName) {
        final JSONScanner lexer = parser.getLexer();
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            return null;
        }

        Map<Object, Object> map = createMap(type);

        if (lexer.token() == JSONToken.RBRACE) {
            lexer.nextToken(JSONToken.COMMA);
            return map;
        }

        return deserialze(parser, type, fieldName, map);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Object deserialze(JSONParser parser, Type type, Object fieldName, Map map) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type keyType = parameterizedType.getActualTypeArguments()[0];
            Type valueType = parameterizedType.getActualTypeArguments()[1];

            if (String.class == keyType) return ObjectDeserializer.instance.parseMap(parser, map, valueType, fieldName);
            else return ObjectDeserializer.instance.parseMap(parser, map, keyType, valueType, fieldName);
        } else return parser.parseObject(map, fieldName);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<Object, Object> createMap(Type type) {
        if (type == Properties.class) return new Properties();

        if (type == Hashtable.class) return new Hashtable();

        if (type == IdentityHashMap.class) return new IdentityHashMap();

        if (type == SortedMap.class || type == TreeMap.class) return new TreeMap();

        if (type == ConcurrentMap.class || type == ConcurrentHashMap.class) return new ConcurrentHashMap();

        if (type == Map.class) return new AntiCollisionHashMap();

        if (type == HashMap.class) return new HashMap();

        if (type == LinkedHashMap.class) return new LinkedHashMap();

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            return createMap(parameterizedType.getRawType());
        }

        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isInterface()) throw new EsonException("unsupport type " + type);

            try {
                return (Map<Object, Object>) clazz.newInstance();
            } catch (Exception e) {
                throw new EsonException("unsupport type " + type, e);
            }
        }

        throw new EsonException("unsupport type " + type);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }
}
