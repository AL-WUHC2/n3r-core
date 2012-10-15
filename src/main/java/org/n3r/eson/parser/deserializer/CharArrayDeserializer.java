package org.n3r.eson.parser.deserializer;

import java.lang.reflect.Type;

import org.n3r.eson.Eson;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;

public class CharArrayDeserializer implements EsonDeserializable {

    public final static CharArrayDeserializer instance = new CharArrayDeserializer();

    @Override
    public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
        return deserialize(parser);
    }

    @SuppressWarnings("unchecked")
    public static Object deserialize(JSONParser parser) {
        final JSONScanner lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_STRING) {
            String val = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);
            return val.toCharArray();
        }

        if (lexer.token() == JSONToken.LITERAL_INT) {
            Number val = lexer.integerValue();
            lexer.nextToken(JSONToken.COMMA);
            return val.toString().toCharArray();
        }

        Object value = parser.parse();

        if (value == null) return null;

        return new Eson().toString(value).toCharArray();
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

}
