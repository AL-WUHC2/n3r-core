package org.n3r.eson.parser.deserializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.parser.JSONScanner;
import org.n3r.eson.parser.JSONToken;
import org.n3r.eson.utils.TypeUtils;

public class PrimitivesDeserializer {
    public static class CharacterDeserializer implements EsonDeserializable {
        public final static CharacterDeserializer instance = new CharacterDeserializer();

        @Override
        public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
            Object value = parser.parse();

            if (value == null) return null;

            return TypeUtils.castToChar(value);
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_STRING;
        }
    }

    public static class NumberDeserializer implements EsonDeserializable {

        public final static NumberDeserializer instance = new NumberDeserializer();

        @Override
        public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
            final JSONScanner lexer = parser.getLexer();
            if (lexer.token() == JSONToken.LITERAL_INT) {
                if (clazz == double.class || clazz == Double.class) {
                    String val = lexer.numberString();
                    lexer.nextToken(JSONToken.COMMA);
                    return Double.valueOf(Double.parseDouble(val));
                }

                long val = lexer.longValue();
                lexer.nextToken(JSONToken.COMMA);

                if (clazz == double.class || clazz == Double.class) return Double.valueOf(val);
                if (clazz == short.class || clazz == Short.class) return Short.valueOf((short) val);
                if (clazz == byte.class || clazz == Byte.class) return Byte.valueOf((byte) val);

                if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) return Integer.valueOf((int) val);
                return Long.valueOf(val);
            }

            if (lexer.token() == JSONToken.LITERAL_FLOAT) {
                if (clazz == double.class || clazz == Double.class) {
                    String val = lexer.numberString();
                    lexer.nextToken(JSONToken.COMMA);
                    return Double.valueOf(Double.parseDouble(val));
                }

                BigDecimal val = lexer.decimalValue();
                lexer.nextToken(JSONToken.COMMA);

                if (clazz == short.class || clazz == Short.class) return Short.valueOf(val.shortValue());
                if (clazz == byte.class || clazz == Byte.class) return Byte.valueOf(val.byteValue());

                return val;
            }

            Object value = parser.parse();

            if (value == null) return null;
            if (clazz == double.class || clazz == Double.class) return TypeUtils.castToDouble(value);
            if (clazz == short.class || clazz == Short.class) return TypeUtils.castToShort(value);
            if (clazz == byte.class || clazz == Byte.class) return TypeUtils.castToByte(value);

            return TypeUtils.castToBigDecimal(value);
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_INT;
        }
    }

    public static class IntegerDeserializer implements EsonDeserializable {
        public final static IntegerDeserializer instance = new IntegerDeserializer();

        @Override
        public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
            final JSONScanner lexer = parser.getLexer();

            if (lexer.token() == JSONToken.NULL) {
                lexer.nextToken(JSONToken.COMMA);
                return null;
            }

            if (lexer.token() == JSONToken.LITERAL_INT) {
                int val = lexer.intValue();
                lexer.nextToken(JSONToken.COMMA);
                return Integer.valueOf(val);
            }

            if (lexer.token() == JSONToken.LITERAL_FLOAT) {
                BigDecimal decimalValue = lexer.decimalValue();
                lexer.nextToken(JSONToken.COMMA);
                return Integer.valueOf(decimalValue.intValue());
            }

            Object value = parser.parse();

            return TypeUtils.castToInt(value);
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_INT;
        }
    }

    public static class LongDeserializer implements EsonDeserializable {
        public final static LongDeserializer instance = new LongDeserializer();

        @Override
        public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
            final JSONScanner lexer = parser.getLexer();
            if (lexer.token() == JSONToken.LITERAL_INT) {
                long longValue = lexer.longValue();
                lexer.nextToken(JSONToken.COMMA);
                return Long.valueOf(longValue);
            }

            Object value = parser.parse();

            if (value == null) return null;

            return TypeUtils.castToLong(value);
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_INT;
        }
    }

    public static class FloatDeserializer implements EsonDeserializable {

        public final static FloatDeserializer instance = new FloatDeserializer();

        @Override
        public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
            return deserialize(parser);
        }

        public static Object deserialize(JSONParser parser) {
            final JSONScanner lexer = parser.getLexer();
            if (lexer.token() == JSONToken.LITERAL_INT) {
                String val = lexer.numberString();
                lexer.nextToken(JSONToken.COMMA);
                return Float.valueOf(Float.parseFloat(val));
            }

            if (lexer.token() == JSONToken.LITERAL_FLOAT) {
                float val = lexer.floatValue();
                lexer.nextToken(JSONToken.COMMA);
                return Float.valueOf(val);
            }

            Object value = parser.parse();

            if (value == null) { return null; }

            return TypeUtils.castToFloat(value);
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_INT;
        }
    }

    public static class BooleanDeserializer implements EsonDeserializable {
        public final static BooleanDeserializer instance = new BooleanDeserializer();

        @Override
        public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
            return deserialize(parser);
        }

        public static Object deserialize(JSONParser parser) {
            final JSONScanner lexer = parser.getLexer();

            if (lexer.token() == JSONToken.TRUE) {
                lexer.nextToken(JSONToken.COMMA);
                return Boolean.TRUE;
            }

            if (lexer.token() == JSONToken.FALSE) {
                lexer.nextToken(JSONToken.COMMA);
                return Boolean.FALSE;
            }

            if (lexer.token() == JSONToken.LITERAL_INT) {
                int intValue = lexer.intValue();
                lexer.nextToken(JSONToken.COMMA);

                if (intValue == 1) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }

            Object value = parser.parse();

            if (value == null) { return null; }

            return TypeUtils.castToBoolean(value);
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.TRUE;
        }
    }

    public static class BigDecimalDeserializer implements EsonDeserializable {
        public final static BigDecimalDeserializer instance = new BigDecimalDeserializer();

        @Override
        @SuppressWarnings("unchecked")
        public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
            return deserialze(parser);
        }

        @SuppressWarnings("unchecked")
        public static Object deserialze(JSONParser parser) {
            final JSONScanner lexer = parser.getLexer();
            if (lexer.token() == JSONToken.LITERAL_INT) {
                long val = lexer.longValue();
                lexer.nextToken(JSONToken.COMMA);
                return new BigDecimal(val);
            }

            if (lexer.token() == JSONToken.LITERAL_FLOAT) {
                BigDecimal val = lexer.decimalValue();
                lexer.nextToken(JSONToken.COMMA);
                return val;
            }

            Object value = parser.parse();

            if (value == null) return null;

            return TypeUtils.castToBigDecimal(value);
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_INT;
        }
    }

    public static class BigIntegerDeserializer implements EsonDeserializable {

        public final static BigIntegerDeserializer instance = new BigIntegerDeserializer();

        @Override
        public Object deserialize(JSONParser parser, Type clazz, Object fieldName) {
            return deserialze(parser);
        }

        public static Object deserialze(JSONParser parser) {
            final JSONScanner lexer = parser.getLexer();
            if (lexer.token() == JSONToken.LITERAL_INT) {
                String val = lexer.numberString();
                lexer.nextToken(JSONToken.COMMA);
                return new BigInteger(val);
            }

            Object value = parser.parse();

            if (value == null) return null;

            return TypeUtils.castToBigInteger(value);
        }

        @Override
        public int getFastMatchToken() {
            return JSONToken.LITERAL_INT;
        }
    }

}
