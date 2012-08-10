package org.n3r.core.lang;

import javax.xml.bind.DatatypeConverter;

public class RBase64 {
    /**
     * Convert bytes array to Base64 presentations.
     * @param bytes bytes array
     * @return base64 encoded string.
     */
    public static String encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

}
