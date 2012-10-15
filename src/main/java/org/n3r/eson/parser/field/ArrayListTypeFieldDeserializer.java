package org.n3r.eson.parser.field;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.n3r.eson.EsonException;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.FieldInfo;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.parser.ParserMapping;

public class ArrayListTypeFieldDeserializer extends FieldDeserializer {

    private final Type itemType;
    private int itemFastMatchToken;
    private EsonDeserializable deserializer;

    public ArrayListTypeFieldDeserializer(ParserMapping mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);

        Type fieldType = getFieldType();
        if (fieldType instanceof ParameterizedType) itemType = ((ParameterizedType) getFieldType())
                .getActualTypeArguments()[0];
        else itemType = Object.class;
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void parseField(JSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        if (parser.getLexer().token() == JSONToken.NULL) {
            setValue(object, null);
            return;
        }

        ArrayList list = new ArrayList();

        parseArray(parser, objectType, list);

        if (object == null) fieldValues.put(fieldInfo.getName(), list);
        else setValue(object, list);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void parseArray(JSONParser parser, Type objectType, Collection array) {
        Type itemType = this.itemType;;

        if (itemType instanceof TypeVariable && objectType instanceof ParameterizedType) {
            TypeVariable typeVar = (TypeVariable) itemType;
            ParameterizedType paramType = (ParameterizedType) objectType;

            Class<?> objectClass = null;
            if (paramType.getRawType() instanceof Class) objectClass = (Class<?>) paramType.getRawType();

            int paramIndex = -1;
            if (objectClass != null) for (int i = 0, size = objectClass.getTypeParameters().length; i < size; ++i) {
                TypeVariable item = objectClass.getTypeParameters()[i];
                if (item.getName().equals(typeVar.getName())) {
                    paramIndex = i;
                    break;
                }
            }

            if (paramIndex != -1) itemType = paramType.getActualTypeArguments()[paramIndex];
        }

        final JSONScanner lexer = parser.getLexer();

        if (lexer.token() != JSONToken.LBRACKET)
            throw new EsonException("exepct '[', but " + JSONToken.name(lexer.token()));

        if (deserializer == null) {
            deserializer = parser.getConfig().getDeserializer(itemType);
            itemFastMatchToken = deserializer.getFastMatchToken();
        }

        lexer.nextToken(itemFastMatchToken);

        for (int i = 0;; ++i) {
            while (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken();
                continue;
            }

            if (lexer.token() == JSONToken.RBRACKET) break;

            Object val = deserializer.deserialize(parser, itemType, i);
            array.add(val);

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(itemFastMatchToken);
                continue;
            }
        }

        lexer.nextToken(JSONToken.COMMA);
    }

}
