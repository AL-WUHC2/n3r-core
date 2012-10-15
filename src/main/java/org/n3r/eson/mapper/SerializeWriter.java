package org.n3r.eson.mapper;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;

import org.n3r.eson.EsonFeature;
import org.n3r.eson.utils.Base64;
import org.n3r.eson.utils.CharTypes;
import org.n3r.eson.utils.IOUtils;
import org.n3r.eson.utils.SerialWriterStringEncoder;

public final class SerializeWriter {
    // The buffer where data is stored.
    protected char buf[];
    // The number of chars in the buffer.
    protected int count;
    private EsonFeature esonFeature;

    private final static ThreadLocal<SoftReference<char[]>> bufLocal = new ThreadLocal<SoftReference<char[]>>();

    public SerializeWriter(EsonFeature esonFeature) {
        this.esonFeature = esonFeature;

        SoftReference<char[]> ref = bufLocal.get();

        if (ref != null) {
            buf = ref.get();
            bufLocal.set(null);
        }

        if (buf == null) buf = new char[1024];
    }

    public void expandCapacity(int minimumCapacity) {
        int newCapacity = buf.length * 3 / 2 + 1;

        if (newCapacity < minimumCapacity) newCapacity = minimumCapacity;
        char newValue[] = new char[newCapacity];
        System.arraycopy(buf, 0, newValue, 0, count);
        buf = newValue;
    }

    /**
     * Resets the buffer so that you can use it again without throwing away the already allocated buffer.
     */
    public void reset() {
        count = 0;
    }

    /**
     * Returns a copy of the input data.
     *
     * @return an array of chars copied from the input data.
     */
    public char[] toCharArray() {
        char[] newValue = new char[count];
        System.arraycopy(buf, 0, newValue, 0, count);
        return newValue;
    }

    public byte[] toBytes(String charsetName) {
        if (charsetName == null) charsetName = "UTF-8";

        Charset cs = Charset.forName(charsetName);
        SerialWriterStringEncoder encoder = new SerialWriterStringEncoder(cs);

        return encoder.encode(buf, 0, count);
    }

    /**
     * Returns the current size of the buffer.
     *
     * @return an int representing the current size of the buffer.
     */
    public int size() {
        return count;
    }

    /**
     * Converts input data to a string.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return new String(buf, 0, count);
    }

    public void close() {
        if (buf.length <= 1024 * 8) bufLocal.set(new SoftReference<char[]>(buf));

        buf = null;
    }

    public void writeByteArray(byte[] bytes) {
        int bytesLen = bytes.length;
        if (bytesLen == 0) {
            writeValue("");
            return;
        }

        final char[] CA = Base64.CA;

        int eLen = bytesLen / 3 * 3; // Length of even 24-bits.
        int charsLen = (bytesLen - 1) / 3 + 1 << 2; // base64 character count
        // char[] chars = new char[charsLen];
        int offset = count;
        char quote = esonFeature.getValQuote();
        int newcount = count + charsLen + (quote == '\0' ? 0 : 2);
        if (newcount > buf.length) expandCapacity(newcount);
        count = newcount;
        if (quote != '\0') buf[offset++] = quote;

        // Encode even 24-bits
        for (int s = 0, d = offset; s < eLen;) {
            // Copy next three bytes into lower 24 bits of int, paying attension to sign.
            int i = (bytes[s++] & 0xff) << 16 | (bytes[s++] & 0xff) << 8 | bytes[s++] & 0xff;

            // Encode the int into four chars
            buf[d++] = CA[i >>> 18 & 0x3f];
            buf[d++] = CA[i >>> 12 & 0x3f];
            buf[d++] = CA[i >>> 6 & 0x3f];
            buf[d++] = CA[i & 0x3f];
        }

        // Pad and encode last bits if source isn't even 24 bits.
        int left = bytesLen - eLen; // 0 - 2.
        int fix = quote != '\0' ? 0 : 1;
        if (left > 0) {
            // Prepare the int
            int i = (bytes[eLen] & 0xff) << 10 | (left == 2 ? (bytes[bytesLen - 1] & 0xff) << 2 : 0);

            // Set last four chars
            buf[newcount - 5 + fix] = CA[i >> 12];
            buf[newcount - 4 + fix] = CA[i >>> 6 & 0x3f];
            buf[newcount - 3 + fix] = left == 2 ? CA[i & 0x3f] : '=';
            buf[newcount - 2 + fix] = '=';
        }

        if (quote != '\0') buf[newcount - 1] = quote;
    }

    public void writeShortArray(short[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) totalSize++;
            short val = array[i];
            int size = IOUtils.stringSize(val);
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) expandCapacity(newcount);

        buf[count] = '[';

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) buf[currentSize++] = ',';

            short val = array[i];
            currentSize += sizeArray[i];
            IOUtils.getChars(val, currentSize, buf);
        }
        buf[currentSize] = ']';

        count = newcount;
    }

    public void writeIntArray(int[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) totalSize++;
            int val = array[i];
            int size;
            if (val == Integer.MIN_VALUE) size = "-2147483648".length();
            else size = val < 0 ? IOUtils.stringSize(-val) + 1 : IOUtils.stringSize(val);
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) expandCapacity(newcount);

        buf[count] = '[';

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) buf[currentSize++] = ',';

            int val = array[i];
            if (val == Integer.MIN_VALUE) {
                System.arraycopy("-2147483648".toCharArray(), 0, buf, currentSize, sizeArray[i]);
                currentSize += sizeArray[i];
            } else {
                currentSize += sizeArray[i];
                IOUtils.getChars(val, currentSize, buf);
            }
        }
        buf[currentSize] = ']';

        count = newcount;
    }

    public void writeLongArray(long[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) totalSize++;
            long val = array[i];
            int size;
            if (val == Long.MIN_VALUE) size = "-9223372036854775808".length();
            else size = val < 0 ? IOUtils.stringSize(-val) + 1 : IOUtils.stringSize(val);
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) expandCapacity(newcount);

        buf[count] = '[';

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) buf[currentSize++] = ',';

            long val = array[i];
            if (val == Long.MIN_VALUE) {
                System.arraycopy("-9223372036854775808".toCharArray(), 0, buf, currentSize, sizeArray[i]);
                currentSize += sizeArray[i];
            } else {
                currentSize += sizeArray[i];
                IOUtils.getChars(val, currentSize, buf);
            }
        }
        buf[currentSize] = ']';

        count = newcount;
    }

    public void writeBooleanArray(boolean[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) totalSize++;
            boolean val = array[i];
            int size;
            if (val) size = 4; // "true".length();
            else size = 5; // "false".length();
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) expandCapacity(newcount);

        buf[count] = '[';

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) buf[currentSize++] = ',';

            boolean val = array[i];
            if (val) {
                // System.arraycopy("true".toCharArray(), 0, buf, currentSize,
                // 4);
                buf[currentSize++] = 't';
                buf[currentSize++] = 'r';
                buf[currentSize++] = 'u';
                buf[currentSize++] = 'e';
            } else {
                buf[currentSize++] = 'f';
                buf[currentSize++] = 'a';
                buf[currentSize++] = 'l';
                buf[currentSize++] = 's';
                buf[currentSize++] = 'e';
            }
        }
        buf[currentSize] = ']';

        count = newcount;
    }

    public void writeInt(int i) {
        if (i == Integer.MIN_VALUE) {
            write("-2147483648");
            return;
        }

        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

        int newcount = count + size;
        if (newcount > buf.length) expandCapacity(newcount);

        IOUtils.getChars(i, newcount, buf);

        count = newcount;
    }

    public void writeIntAndChar(int i, char c) {
        if (i == Integer.MIN_VALUE) {
            write("-2147483648");
            write(c);
            return;
        }

        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

        int newcount0 = count + size;
        int newcount1 = newcount0 + 1;

        if (newcount1 > buf.length) expandCapacity(newcount1);

        IOUtils.getChars(i, newcount0, buf);
        buf[newcount0] = c;

        count = newcount1;
    }

    public void writeLongAndChar(long i, char c) {
        if (i == Long.MIN_VALUE) {
            write("-9223372036854775808");
            write(c);
            return;
        }

        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

        int newcount0 = count + size;
        int newcount1 = newcount0 + 1;

        if (newcount1 > buf.length) expandCapacity(newcount1);

        IOUtils.getChars(i, newcount0, buf);
        buf[newcount0] = c;

        count = newcount1;
    }

    public void writeLong(long i) {
        if (i == Long.MIN_VALUE) {
            write("-9223372036854775808");
            return;
        }

        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

        int newcount = count + size;
        if (newcount > buf.length) expandCapacity(newcount);

        IOUtils.getChars(i, newcount, buf);

        count = newcount;
    }

    public void writeValue(boolean[] array) throws IOException {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) totalSize++;
            boolean val = array[i];
            int size;
            if (val) size = 4; // "true".length();
            else size = 5; // "false".length();
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) expandCapacity(newcount);

        buf[count] = '[';

        int currentPos = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) buf[currentPos++] = ',';

            boolean val = array[i];
            if (val) {
                buf[currentPos++] = 't';
                buf[currentPos++] = 'r';
                buf[currentPos++] = 'u';
                buf[currentPos++] = 'e';
            } else {
                buf[currentPos++] = 'f';
                buf[currentPos++] = 'a';
                buf[currentPos++] = 'l';
                buf[currentPos++] = 's';
                buf[currentPos++] = 'e';
            }
        }
        buf[currentPos] = ']';

        count = newcount;
    }

    public void writeValue(int i) {
        if (i == Integer.MIN_VALUE) {
            write("-2147483648");
            return;
        }

        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

        int newcount = count + size;
        if (newcount > buf.length) expandCapacity(newcount);

        IOUtils.getChars(i, newcount, buf);

        count = newcount;
    }

    public void writeValue(short[] array) throws IOException {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) totalSize++;
            short val = array[i];
            int size = IOUtils.stringSize(val);
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) expandCapacity(newcount);

        buf[count] = '[';

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) buf[currentSize++] = ',';

            short val = array[i];
            currentSize += sizeArray[i];
            IOUtils.getChars(val, currentSize, buf);
        }
        buf[currentSize] = ']';

        count = newcount;
    }

    public void writeValue(byte[] bytes) {
        int bytesLen = bytes.length;
        if (bytesLen == 0) {
            write("\"\"");
            return;
        }

        final char[] CA = Base64.CA;

        int eLen = bytesLen / 3 * 3; // Length of even 24-bits.
        int charsLen = (bytesLen - 1) / 3 + 1 << 2; // base64 character count
        int offset = count;
        int newcount = count + charsLen + 2;
        if (newcount > buf.length) expandCapacity(newcount);
        count = newcount;
        buf[offset++] = '\"';

        // Encode even 24-bits
        for (int s = 0, d = offset; s < eLen;) {
            // Copy next three bytes into lower 24 bits of int, paying attension to sign.
            int i = (bytes[s++] & 0xff) << 16 | (bytes[s++] & 0xff) << 8 | bytes[s++] & 0xff;

            // Encode the int into four chars
            buf[d++] = CA[i >>> 18 & 0x3f];
            buf[d++] = CA[i >>> 12 & 0x3f];
            buf[d++] = CA[i >>> 6 & 0x3f];
            buf[d++] = CA[i & 0x3f];
        }

        // Pad and encode last bits if source isn't even 24 bits.
        int left = bytesLen - eLen; // 0 - 2.
        if (left > 0) {
            // Prepare the int
            int i = (bytes[eLen] & 0xff) << 10 | (left == 2 ? (bytes[bytesLen - 1] & 0xff) << 2 : 0);

            // Set last four chars
            buf[newcount - 5] = CA[i >> 12];
            buf[newcount - 4] = CA[i >>> 6 & 0x3f];
            buf[newcount - 3] = left == 2 ? CA[i & 0x3f] : '=';
            buf[newcount - 2] = '=';
        }
        buf[newcount - 1] = '\"';
    }

    public void writeValue(int[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) totalSize++;
            int val = array[i];
            int size;
            if (val == Integer.MIN_VALUE) size = "-2147483648".length();
            else size = val < 0 ? IOUtils.stringSize(-val) + 1 : IOUtils.stringSize(val);
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) expandCapacity(newcount);

        buf[count] = '[';

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) buf[currentSize++] = ',';

            int val = array[i];
            if (val == Integer.MIN_VALUE) {
                System.arraycopy("-2147483648".toCharArray(), 0, buf, currentSize, sizeArray[i]);
                currentSize += sizeArray[i];
            } else {
                currentSize += sizeArray[i];
                IOUtils.getChars(val, currentSize, buf);
            }
        }
        buf[currentSize] = ']';

        count = newcount;
    }

    public void write(String text) {
        int length = text.length();
        int newcount = count + length;
        if (newcount > buf.length) expandCapacity(newcount);
        text.getChars(0, length, buf, count);
        count = newcount;
    }

    public void write(char c) {
        int newcount = count + 1;
        if (newcount > buf.length) expandCapacity(newcount);
        buf[count] = c;
        count = newcount;
    }

    public void writeValue(int i, char c) {
        if (i == Integer.MIN_VALUE) {
            write("-2147483648");
            write(c);
            return;
        }

        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

        int newcount0 = count + size;
        int newcount1 = newcount0 + 1;

        if (newcount1 > buf.length) expandCapacity(newcount1);

        IOUtils.getChars(i, newcount0, buf);
        buf[newcount0] = c;

        count = newcount1;
    }

    public void writeValue(long i, char c) throws IOException {
        if (i == Long.MIN_VALUE) {
            write("-9223372036854775808");
            write(c);
            return;
        }

        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

        int newcount0 = count + size;
        int newcount1 = newcount0 + 1;

        if (newcount1 > buf.length) expandCapacity(newcount1);

        IOUtils.getChars(i, newcount0, buf);
        buf[newcount0] = c;

        count = newcount1;
    }

    public void writeValue(long i) {
        if (i == Long.MIN_VALUE) {
            write("-9223372036854775808");
            return;
        }

        int size = i < 0 ? IOUtils.stringSize(-i) + 1 : IOUtils.stringSize(i);

        int newcount = count + size;
        if (newcount > buf.length) expandCapacity(newcount);

        IOUtils.getChars(i, newcount, buf);

        count = newcount;
    }

    public void writeNull() {
        int newcount = count + 4;
        if (newcount > buf.length) expandCapacity(newcount);
        buf[count] = 'n';
        buf[count + 1] = 'u';
        buf[count + 2] = 'l';
        buf[count + 3] = 'l';
        count = newcount;
    }

    public void writeValue(long[] array) {
        int[] sizeArray = new int[array.length];
        int totalSize = 2;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) totalSize++;
            long val = array[i];
            int size;
            if (val == Long.MIN_VALUE) size = "-9223372036854775808".length();
            else size = val < 0 ? IOUtils.stringSize(-val) + 1 : IOUtils.stringSize(val);
            sizeArray[i] = size;
            totalSize += size;
        }

        int newcount = count + totalSize;
        if (newcount > buf.length) expandCapacity(newcount);

        buf[count] = '[';

        int currentSize = count + 1;
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) buf[currentSize++] = ',';

            long val = array[i];
            if (val == Long.MIN_VALUE) {
                System.arraycopy("-9223372036854775808".toCharArray(), 0, buf, currentSize, sizeArray[i]);
                currentSize += sizeArray[i];
            } else {
                currentSize += sizeArray[i];
                IOUtils.getChars(val, currentSize, buf);
            }
        }
        buf[currentSize] = ']';

        count = newcount;
    }

    public void writeKey(String text) {
        if (text == null) text = "null";
        writeValue(text, esonFeature.getKeyQuote(), ':');
    }

    public void writeValue(String text) {
        char quote = esonFeature.getValQuote();
        if (quote == '\0') {
            if (text.length() > 0) {
                char c = text.charAt(0);
                if (c == 'n' && "null".equals(text)
                        || c == 't' && "true".equals(text)
                        || c == 'f' && "false".equals(text)
                        || isBlankAtBeginOrEnd(text))
                    quote = '\"';
            }
            else {
                quote = '\"';
            }
        }

        writeValue(text, quote, '\0');
    }

    private boolean isBlankAtBeginOrEnd(String text) {
        return CharTypes.isWhitespace(text.charAt(0))
                || CharTypes.isWhitespace(text.charAt(text.length() - 1));
    }

    public void writeValue(String text, char quote, char seperator) {
        int len = text.length();
        int newcount = count + len + (quote == '\0' ? 0 : 2) + (seperator == '\0' ? 0 : 1);
        if (newcount > buf.length) expandCapacity(newcount);

        if (quote != '\0') buf[count] = quote;

        int start = count + (quote == '\0' ? 0 : 1);
        int end = start + len;

        text.getChars(0, len, buf, start);
        count = newcount;

        for (int i = start; i < end; ++i) {
            char ch = buf[i];
            if (CharTypes.isSpecial(ch)) {
                newcount++;
                if (newcount > buf.length) expandCapacity(newcount);
                count = newcount;

                System.arraycopy(buf, i + 1, buf, i + 2, end - i - 1);
                buf[i] = '\\';
                buf[++i] = CharTypes.escapeSpecialChars[ch];
                end++;
            }
        }

        if (quote != '\0') buf[count - 1 - (seperator == '\0' ? 0 : 1)] = quote;
        if (seperator != '\0') buf[count - 1] = seperator;
    }

    public boolean isEnabled(int feature) {
        return esonFeature.isEnabled(feature);
    }

    //    public DateFormat getDateFormat() {
    //        return esonFeature.getDateFormat();
    //    }

}
