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

public class StringFieldDeserializer extends FieldDeserializer {

    private final EsonDeserializable fieldValueDeserilizer;

    public StringFieldDeserializer(ParserMapping config, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);

        fieldValueDeserilizer = config.getDeserializer(fieldInfo);
    }

    @Override
    public void parseField(JSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        String value;

        final JSONScanner lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_STRING) {
            value = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);
        } else {

            Object obj = parser.parse();

            if (obj == null) value = null;
            else value = obj.toString();
        }

        if (object == null) fieldValues.put(fieldInfo.getName(), value);
        else setValue(object, value);
    }

    @Override
    public int getFastMatchToken() {
        return fieldValueDeserilizer.getFastMatchToken();
    }
}
