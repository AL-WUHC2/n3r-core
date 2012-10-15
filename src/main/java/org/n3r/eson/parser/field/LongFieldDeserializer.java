package org.n3r.eson.parser.field;

import java.lang.reflect.Type;
import java.util.Map;

import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.FieldInfo;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.parser.ParserMapping;
import org.n3r.eson.utils.TypeUtils;

public class LongFieldDeserializer extends FieldDeserializer {

    private final EsonDeserializable fieldValueDeserilizer;

    public LongFieldDeserializer(ParserMapping mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);

        fieldValueDeserilizer = mapping.getDeserializer(fieldInfo);
    }

    @Override
    public void parseField(JSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        Long value;

        final JSONScanner lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_INT) {
            long val = lexer.longValue();
            lexer.nextToken(JSONToken.COMMA);
            if (object == null) fieldValues.put(fieldInfo.getName(), val);
            else setValue(object, val);
            return;
        } else if (lexer.token() == JSONToken.NULL) {
            value = null;
            lexer.nextToken(JSONToken.COMMA);

        } else {
            Object obj = parser.parse();

            value = TypeUtils.castToLong(obj);
        }

        if (value == null && getFieldClass() == long.class) // skip
            return;

        if (object == null) fieldValues.put(fieldInfo.getName(), value);
        else setValue(object, value);
    }

    @Override
    public int getFastMatchToken() {
        return fieldValueDeserilizer.getFastMatchToken();
    }
}
