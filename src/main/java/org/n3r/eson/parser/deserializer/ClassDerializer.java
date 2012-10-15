package org.n3r.eson.parser.deserializer;

import java.lang.reflect.Type;

import org.n3r.eson.EsonException;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.utils.TypeUtils;

public class ClassDerializer implements EsonDeserializable {

    public final static ClassDerializer instance = new ClassDerializer();

    public ClassDerializer() {}

    @Override
    public Object deserialize(JSONParser parser, Type type, Object fieldName) {
        JSONScanner lexer = parser.getLexer();

        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken();
            return null;
        }

        if (lexer.token() != JSONToken.LITERAL_STRING) throw new EsonException("expect className");
        String className = lexer.stringVal();
        lexer.nextToken(JSONToken.COMMA);

        return TypeUtils.loadClass(className);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

}
