package org.n3r.core.lang;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

public class RStream {
    public static InputStream toInputStream(ByteArrayOutputStream out) {
        return new ByteArrayInputStream(/* ByteArrayOutputStream */out.toByteArray());
    }

    public static Reader toReader(StringWriter out) {
        return new StringReader(/* StringWriter */out.toString());
    }
}
