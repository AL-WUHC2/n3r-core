package org.n3r.core.lang;

import org.apache.commons.lang3.StringUtils;

public class RVersion {
    public static double JAVA_VERSION;
    static {
        String version = System.getProperty("java.version");
        int pos = StringUtils.ordinalIndexOf(version, ".", 2);

        JAVA_VERSION = Double.parseDouble(version.substring(0, pos));
    }

    /**
     * Get current java vm's version.
     * @return double presentation of java version.
     */
    public static double getVersion() {
        return JAVA_VERSION;
    }
}
