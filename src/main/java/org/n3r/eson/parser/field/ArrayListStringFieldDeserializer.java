package org.n3r.eson.parser.field;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.FieldInfo;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.parser.ParserMapping;
import org.n3r.eson.parser.deserializer.ArrayListStringDeserializer;

public class ArrayListStringFieldDeserializer extends FieldDeserializer {

    public ArrayListStringFieldDeserializer(ParserMapping mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);

    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }

    @Override
    public void parseField(JSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        ArrayList<Object> list;

        final JSONScanner lexer = parser.getLexer();
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            list = null;
        } else {
            list = new ArrayList<Object>();

            ArrayListStringDeserializer.parseArray(parser, list);
        }
        if (object == null) fieldValues.put(fieldInfo.getName(), list);
        else setValue(object, list);
    }
}
