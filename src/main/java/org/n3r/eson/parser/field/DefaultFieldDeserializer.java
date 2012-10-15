package org.n3r.eson.parser.field;

import java.lang.reflect.Type;
import java.util.Map;

import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.FieldInfo;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.parser.ParserMapping;

public class DefaultFieldDeserializer extends FieldDeserializer {

    private EsonDeserializable fieldValueDeserilizer;

    public DefaultFieldDeserializer(ParserMapping mapping, Class<?> clazz, FieldInfo fieldInfo) {
        super(clazz, fieldInfo);
    }

    @Override
    public void parseField(JSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {
        if (fieldValueDeserilizer == null) fieldValueDeserilizer = parser.getConfig().getDeserializer(fieldInfo);

        Object value = fieldValueDeserilizer.deserialize(parser, getFieldType(), fieldInfo.getName());
        if (object == null) fieldValues.put(fieldInfo.getName(), value);
        else setValue(object, value);
    }

    @Override
    public int getFastMatchToken() {
        if (fieldValueDeserilizer != null) return fieldValueDeserilizer.getFastMatchToken();

        return JSONToken.LITERAL_INT;
    }
}
