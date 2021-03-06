package org.n3r.eson.parser.deserializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.n3r.eson.EsonException;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;

public class ArrayListStringDeserializer implements EsonDeserializable {

    public final static ArrayListStringDeserializer instance = new ArrayListStringDeserializer();

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object deserialize(JSONParser parser, Type type, Object fieldName) {

        final JSONScanner lexer = parser.getLexer();
        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            return null;
        }

        Collection array = null;

        if (type == Set.class || type == HashSet.class) array = new HashSet();
        else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            if (rawType == Set.class || rawType == HashSet.class) array = new HashSet();
        }

        if (array == null) array = new ArrayList();

        parseArray(parser, array);

        return array;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void parseArray(JSONParser parser, Collection array) {
        JSONScanner lexer = parser.getLexer();

        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            return;
        }

        if (lexer.token() == JSONToken.SET) lexer.nextToken();

        if (lexer.token() != JSONToken.LBRACKET) throw new EsonException("exepct '[', but " + lexer.token());

        lexer.nextToken(JSONToken.LITERAL_STRING);

        for (;;) {

            while (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken();
                continue;
            }

            if (lexer.token() == JSONToken.RBRACKET) break;

            String value;
            if (lexer.token() == JSONToken.LITERAL_STRING) {
                value = lexer.stringVal();
                lexer.nextToken(JSONToken.COMMA);
            } else {
                Object obj = parser.parse();
                if (obj == null) value = null;
                else value = obj.toString();
            }

            array.add(value);

            if (lexer.token() == JSONToken.COMMA) {
                lexer.nextToken(JSONToken.LITERAL_STRING);
                continue;
            }
        }

        lexer.nextToken(JSONToken.COMMA);
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LBRACKET;
    }
}
