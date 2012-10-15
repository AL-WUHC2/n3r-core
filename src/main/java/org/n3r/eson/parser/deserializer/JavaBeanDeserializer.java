package org.n3r.eson.parser.deserializer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.n3r.eson.EsonException;
import org.n3r.eson.JSONObject;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.FieldInfo;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.parser.ParserMapping;
import org.n3r.eson.utils.DeserializeBeanInfo;

public class JavaBeanDeserializer implements EsonDeserializable {
    private final Map<String, FieldDeserializer> feildDeserializerMap = new IdentityHashMap<String, FieldDeserializer>();
    private final List<FieldDeserializer> fieldDeserializers = new ArrayList<FieldDeserializer>();

    private final Class<?> clazz;
    private final Type type;

    private DeserializeBeanInfo beanInfo;

    public JavaBeanDeserializer(DeserializeBeanInfo beanInfo) {
        this.beanInfo = beanInfo;
        clazz = beanInfo.getClazz();
        type = beanInfo.getType();
    }

    public JavaBeanDeserializer(ParserMapping config, Class<?> clazz) {
        this(config, clazz, clazz);
    }

    public JavaBeanDeserializer(ParserMapping config, Class<?> clazz, Type type) {
        this.clazz = clazz;
        this.type = type;

        beanInfo = DeserializeBeanInfo.computeSetters(clazz, type);

        for (FieldInfo fieldInfo : beanInfo.getFieldList())
            addFieldDeserializer(config, clazz, fieldInfo);
    }

    public Map<String, FieldDeserializer> getFieldDeserializerMap() {
        return feildDeserializerMap;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Type getType() {
        return type;
    }

    private void addFieldDeserializer(ParserMapping mapping, Class<?> clazz, FieldInfo fieldInfo) {
        FieldDeserializer fieldDeserializer = createFieldDeserializer(mapping, clazz, fieldInfo);

        feildDeserializerMap.put(fieldInfo.getName().intern(), fieldDeserializer);
        fieldDeserializers.add(fieldDeserializer);
    }

    public FieldDeserializer createFieldDeserializer(ParserMapping mapping, Class<?> clazz, FieldInfo fieldInfo) {
        return mapping.createFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public Object createInstance(JSONParser parser, Type type) {
        if (type instanceof Class) if (clazz.isInterface()) {
            Class<?> clazz = (Class<?>) type;
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            final JSONObject obj = new JSONObject();
            Object proxy = Proxy.newProxyInstance(loader, new Class<?>[] { clazz }, obj);
            return proxy;
        }

        if (beanInfo.getDefaultConstructor() != null) try {
            Constructor<?> constructor = beanInfo.getDefaultConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new EsonException("create instance error, class " + clazz.getName(), e);
        }

        if (beanInfo.getObjectInstantiator() != null) try {
            return beanInfo.getObjectInstantiator().newInstance();
        } catch (Exception e) {
            throw new EsonException("create instance error, class " + clazz.getName(), e);
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(JSONParser parser, Type type, Object fieldName) {
        JSONScanner lexer = parser.getLexer(); // xxx

        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            return null;
        }

        Object object = null;

        Map<String, Object> fieldValues = null;

        if (lexer.token() == JSONToken.RBRACE) {
            lexer.nextToken(JSONToken.COMMA);
            return createInstance(parser, type);
        }

        if (lexer.token() != JSONToken.LBRACE && lexer.token() != JSONToken.COMMA)
            throw new EsonException("syntax error, expect {, actual " + lexer.tokenName());

        if (parser.getResolveStatus() == JSONParser.TypeNameRedirect) parser.setResolveStatus(JSONParser.NONE);

        for (;;) {

            String key = lexer.scanSymbol(parser.getSymbolTable());

            if (key == null) {
                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken(JSONToken.COMMA);
                    break;
                }
                if (lexer.token() == JSONToken.COMMA) continue;
            }

            if (object == null && fieldValues == null) {
                object = createInstance(parser, type);
                if (object == null) fieldValues = new HashMap<String, Object>(fieldDeserializers.size());
            }

            boolean match = parseField(parser, key, object, type, fieldValues);
            if (!match) {
                if (lexer.token() == JSONToken.RBRACE) {
                    lexer.nextToken();
                    break;
                }

                continue;
            }

            if (lexer.token() == JSONToken.COMMA) continue;

            if (lexer.token() == JSONToken.RBRACE) {
                lexer.nextToken(JSONToken.COMMA);
                break;
            }

            if (lexer.token() == JSONToken.IDENTIFIER || lexer.token() == JSONToken.ERROR)
                throw new EsonException("syntax error, unexpect token " + JSONToken.name(lexer.token()));
        }

        if (object == null) {
            if (fieldValues == null) {
                object = createInstance(parser, type);
                return object;
            }

            List<FieldInfo> fieldInfoList = beanInfo.getFieldList();
            int size = fieldInfoList.size();
            Object[] params = new Object[size];
            for (int i = 0; i < size; ++i) {
                FieldInfo fieldInfo = fieldInfoList.get(i);
                params[i] = fieldValues.get(fieldInfo.getName());
            }

            if (beanInfo.getCreatorConstructor() != null) try {
                object = beanInfo.getCreatorConstructor().newInstance(params);
            } catch (Exception e) {
                throw new EsonException("create instance error, "
                        + beanInfo.getCreatorConstructor().toGenericString(), e);
            }
            else if (beanInfo.getFactoryMethod() != null) try {
                object = beanInfo.getFactoryMethod().invoke(null, params);
            } catch (Exception e) {
                throw new EsonException("create factory method error, "
                        + beanInfo.getFactoryMethod().toString(), e);
            }
        }

        return object;
    }

    public boolean parseField(JSONParser parser, String key, Object object, Type objectType,
            Map<String, Object> fieldValues) {
        JSONScanner lexer = parser.getLexer(); // xxx

        FieldDeserializer fieldDeserializer = feildDeserializerMap.get(key);

        if (fieldDeserializer == null)
            for (Map.Entry<String, FieldDeserializer> entry : feildDeserializerMap.entrySet())
                if (entry.getKey().equalsIgnoreCase(key)) {
                    fieldDeserializer = entry.getValue();
                    break;
                }

        if (fieldDeserializer == null) {
            lexer.nextTokenWithColon();
            parser.parse(); // skip

            return false;
        }

        lexer.nextTokenWithColon(fieldDeserializer.getFastMatchToken());

        fieldDeserializer.parseField(parser, object, objectType, fieldValues);

        return true;
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACE;
    }

}
