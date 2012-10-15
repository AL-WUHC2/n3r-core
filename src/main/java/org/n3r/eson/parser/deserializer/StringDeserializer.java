package org.n3r.eson.parser.deserializer;

import java.lang.reflect.Type;

import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;

public class StringDeserializer implements EsonDeserializable {

    public final static StringDeserializer instance = new StringDeserializer();

    @Override
    public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
        final JSONScanner lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_STRING) {
            String val = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);
            return val;
        }

        if (lexer.token() == JSONToken.LITERAL_INT) {
            String val = lexer.numberString();
            lexer.nextToken(JSONToken.COMMA);
            return val;
        }

        Object value = parser.parse();

        if (value == null) return null;

        return value.toString();
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

}
