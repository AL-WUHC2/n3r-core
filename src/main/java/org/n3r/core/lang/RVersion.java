package org.n3r.core.lang;

public class RVersion {
    public static double JAVA_VERSION = getVersion();

    /**
     * Get current java vm's version.
     * @return double presentation of java version.
     */
    public static double getVersion() {
        String version = System.getProperty("java.version");
        int pos = 0, count = 0;
        for (; pos < version.length() && count < 2;) {
            if (version.charAt(pos++) == '.') count++;
        }
        return Double.parseDouble(version.substring(0, pos - 1));
    }
}
