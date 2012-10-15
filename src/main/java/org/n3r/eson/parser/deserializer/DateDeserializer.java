package org.n3r.eson.parser.deserializer;

import java.lang.reflect.Type;
import java.util.Date;

import org.n3r.core.lang.RDate;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;

public class DateDeserializer implements EsonDeserializable {
    public final static DateDeserializer instance = new DateDeserializer();

    @Override
    public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
        JSONScanner lexer = parser.getLexer();

        if (lexer.token() == JSONToken.LITERAL_INT) {
            long val = lexer.longValue();
            lexer.nextToken(JSONToken.COMMA);

            return new Date(val);
        } else if (lexer.token() == JSONToken.LITERAL_STRING) {
            String strVal = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);

            return RDate.parse(strVal);
        }

        return null;
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }

}
