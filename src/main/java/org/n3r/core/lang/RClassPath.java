package org.n3r.core.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

public class RClassPath {
    public static String toStr(String resClasspath) {
        return toStr(resClasspath, Charsets.UTF_8);
    }

    public static String toStr(String resClasspath, Charset charset) {
        InputStream is = toInputStream(resClasspath);
        try {
            return CharStreams.toString(new InputStreamReader(is, charset));
        }
        catch (IOException e) {
            throw Throwables.propagate(e);
        }
        finally {
            Closeables.closeQuietly(is);
        }
    }

    public static InputStream toInputStream(String resClasspath) {
        return RClassPath.class.getResourceAsStream(resClasspath);
    }
}
