package org.n3r.core.lang;

import javax.xml.bind.DatatypeConverter;

public class RHex {

    public static String encode(byte[] expected) {
        return DatatypeConverter.printHexBinary(expected);
    }

}
