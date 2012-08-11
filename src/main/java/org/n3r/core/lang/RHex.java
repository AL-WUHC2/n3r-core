package org.n3r.core.lang;

import javax.xml.bind.DatatypeConverter;

public class RHex {

    /**
     * convert byte array to HEX presentation.
     * @param bytes byte array
     * @return HEX presentation string
     */
    public static String encode(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes);
    }

    /**
     * convert HEX presentation to byte array.
     * @param hexStr HEX string
     * @return byte array
     */
    public static byte[] decode(String hexStr) {
        return DatatypeConverter.parseHexBinary(hexStr);
    }
}
