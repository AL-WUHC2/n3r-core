package org.n3r.core.lang;

import javax.xml.bind.DatatypeConverter;

public class RBase64 {
    /**
     * Convert byte array to Base64 presentations.
     * @param bytes byte array
     * @return base64 encoded string.
     */
    public static String encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    /**
     * Convert base64 presentation String to byte array.
     * @param base64str base64 presentation string
     * @return byte array
     */
    public static byte[] decode(String base64str) {
        return DatatypeConverter.parseBase64Binary(base64str);
    }
}
