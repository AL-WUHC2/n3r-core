package org.n3r.eson.parser.field;

import java.lang.reflect.Type;
import java.util.Map;

import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.FieldInfo;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.parser.ParserMapping;
import org.n3r.eson.utils.TypeUtils;

public class IntegerFieldDeserializer extends FieldDeserializer {

    public IntegerFieldDeserializer(ParserMapping mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);
    }

    @Override
    public void parseField(JSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        Integer value;

        final JSONScanner lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_INT) {
            int val = lexer.intValue();
            lexer.nextToken(JSONToken.COMMA);
            if (object == null) fieldValues.put(fieldInfo.getName(), val);
            else setValue(object, val);
            return;
        } else if (lexer.token() == JSONToken.NULL) {
            value = null;
            lexer.nextToken(JSONToken.COMMA);
        } else {
            Object obj = parser.parse();

            value = TypeUtils.castToInt(obj);
        }

        if (value == null && getFieldClass() == int.class) // skip
            return;

        if (object == null) fieldValues.put(fieldInfo.getName(), value);
        else setValue(object, value);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
