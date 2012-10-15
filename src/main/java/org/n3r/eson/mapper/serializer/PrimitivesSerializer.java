package org.n3r.eson.mapper.serializer;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;

public class PrimitivesSerializer {

    public static class ShortSerializer implements EsonSerializable {
        public static ShortSerializer instance = new ShortSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter writer = serializer.getWriter();

            if (object == null) writer.writeNull();
            else writer.writeInt(((Number) object).shortValue());
        }
    }

    public static class IntegerSerializer implements EsonSerializable {
        public static IntegerSerializer instance = new IntegerSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter writer = serializer.getWriter();

            if (object == null) writer.writeNull();
            else writer.writeValue(((Number) object).intValue());
        }
    }

    public static class LongSerializer implements EsonSerializable {
        public static LongSerializer instance = new LongSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter writer = serializer.getWriter();

            if (object == null) writer.writeNull();
            else writer.writeLong(((Long) object).longValue());
        }
    }

    public static class BooleanSerializer implements EsonSerializable {
        public final static BooleanSerializer instance = new BooleanSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter writer = serializer.getWriter();

            if (object == null) writer.writeNull();
            else writer.write(((Boolean) object).booleanValue() ? "true" : "false");
        }

    }

    public static class CharacterSerializer implements EsonSerializable {
        public final static CharacterSerializer instance = new CharacterSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter writer = serializer.getWriter();

            if (object == null) {
                writer.writeNull();
                return;
            }

            Character value = (Character) object;
            char c = value.charValue();
            if (c == 0) writer.writeValue("\u0000");
            else writer.writeValue(value.toString());
        }

    }

    public static class ByteSerializer implements EsonSerializable {
        public static ByteSerializer instance = new ByteSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.writeInt(((Number) object).shortValue());
        }
    }

    public static class FloatSerializer implements EsonSerializable {
        public static FloatSerializer instance = new FloatSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) {
                out.writeNull();
                return;
            }

            float floatValue = ((Float) object).floatValue();

            if (Float.isNaN(floatValue) || Float.isInfinite(floatValue)) {
                out.writeNull();
                return;
            }

            String floatText = Float.toString(floatValue);
            if (floatText.endsWith(".0")) floatText = floatText.substring(0, floatText.length() - 2);
            out.write(floatText);
        }
    }

    public static class DoubleSerializer implements EsonSerializable {
        public static DoubleSerializer instance = new DoubleSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) {
                out.writeNull();
                return;
            }

            double doubleValue = ((Double) object).doubleValue();

            if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
                out.writeNull();
                return;
            }

            String doubleText = Double.toString(doubleValue);
            if (doubleText.endsWith(".0")) doubleText = doubleText.substring(0, doubleText.length() - 2);
            out.write(doubleText);
        }
    }

    public static class BigDecimalSerializer implements EsonSerializable {
        public final static BigDecimalSerializer instance = new BigDecimalSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.write(object.toString());
        }
    }

    public static class BigIntegerSerializer implements EsonSerializable {
        public final static BigIntegerSerializer instance = new BigIntegerSerializer();

        @Override
        public void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.write(object.toString());
        }

    }

    public static class ByteArraySerializer implements EsonSerializable {
        public static ByteArraySerializer instance = new ByteArraySerializer();

        @Override
        public final void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.writeByteArray((byte[]) object);
        }
    }

    public static class ShortArraySerializer implements EsonSerializable {
        public static ShortArraySerializer instance = new ShortArraySerializer();

        @Override
        public final void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.writeShortArray((short[]) object);
        }
    }

    public static class IntArraySerializer implements EsonSerializable {
        public static IntArraySerializer instance = new IntArraySerializer();

        @Override
        public final void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.writeIntArray((int[]) object);
        }
    }

    public static class LongArraySerializer implements EsonSerializable {
        public static LongArraySerializer instance = new LongArraySerializer();

        @Override
        public final void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.writeLongArray((long[]) object);
        }
    }

    public static class FloatArraySerializer implements EsonSerializable {
        public static final FloatArraySerializer instance = new FloatArraySerializer();

        @Override
        public final void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) {
                out.writeNull();
                return;
            }

            float[] array = (float[]) object;
            int size = array.length;
            int end = size - 1;

            if (end == -1) {
                out.write("[]");
                return;
            }

            out.write('[');
            for (int i = 0; i < end; ++i) {
                float item = array[i];

                if (Float.isNaN(item)) out.writeNull();
                else out.write(Float.toString(item));

                out.write(',');
            }

            float item = array[end];

            if (Float.isNaN(item)) out.writeNull();
            else out.write(Float.toString(item));

            out.write(']');
        }
    }

    public static class DoubleArraySerializer implements EsonSerializable {
        public static final DoubleArraySerializer instance = new DoubleArraySerializer();

        @Override
        public final void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) {
                out.writeNull();
                return;
            }

            double[] array = (double[]) object;
            int size = array.length;
            int end = size - 1;

            if (end == -1) {
                out.write("[]");
                return;
            }

            out.write('[');
            for (int i = 0; i < end; ++i) {
                double item = array[i];

                if (Double.isNaN(item)) out.writeNull();
                else out.write(Double.toString(item));

                out.write(',');
            }

            double item = array[end];

            if (Double.isNaN(item)) out.writeNull();
            else out.write(Double.toString(item));

            out.write(']');
        }
    }

    public static class BooleanArraySerializer implements EsonSerializable {
        public static BooleanArraySerializer instance = new BooleanArraySerializer();

        @Override
        public final void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.writeBooleanArray((boolean[]) object);
        }
    }

    public static class CharArraySerializer implements EsonSerializable {
        public static CharArraySerializer instance = new CharArraySerializer();

        @Override
        public final void write(EsonSerializer serializer, Object object) {
            SerializeWriter out = serializer.getWriter();

            if (object == null) out.writeNull();
            else out.writeValue(new String((char[]) object));
        }

    }

}
