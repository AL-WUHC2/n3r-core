package org.n3r.config.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.n3r.config.Configable;
import org.n3r.config.ex.ConfigNotFoundException;
import org.n3r.config.ex.ConfigValueFormatException;

public abstract class BaseConfigable implements Configable {
    private static Pattern numberPattern = Pattern.compile("(-?[0-9]+\\.[0-9]*|[0-9]*\\.[0-9]+|-?[0-9]+).*");

    public int getInt(String key) {
        if (!exists(key)) throw new ConfigNotFoundException(key + " not found in config system");

        String str = getStr(key);
        Matcher matcher = numberPattern.matcher(str);
        if (!matcher.matches()) throw new ConfigValueFormatException(key + "'s value [" + str + "] is not an int");

        String intStr = StringUtils.substringBefore(matcher.group(1), ".");
        if (StringUtils.isEmpty(intStr)) return 0;

        return Integer.valueOf(intStr);
    }

    public long getLong(String key) {
        if (!exists(key)) throw new ConfigNotFoundException(key + " not found in config system");

        String str = getStr(key);
        Matcher matcher = numberPattern.matcher(str);
        if (!matcher.matches()) throw new ConfigValueFormatException(key + "'s value [" + str + "] is not a long");

        String intStr = StringUtils.substringBefore(matcher.group(1), ".");
        if (StringUtils.isEmpty(intStr)) return 0;

        return Long.valueOf(intStr);
    }

    public boolean getBool(String key) {
        if (!exists(key)) throw new ConfigNotFoundException(key + " not found in config system");

        return toBool(getStr(key));
    }

    public float getFloat(String key) {
        if (!exists(key)) throw new ConfigNotFoundException(key + " not found in config system");

        String str = getStr(key);
        Matcher matcher = numberPattern.matcher(str);
        if (!matcher.matches()) throw new ConfigValueFormatException(key + "'s value [" + str + "] is not a float");

        return Float.valueOf(matcher.group(1));
    }

    public double getDouble(String key) {
        if (!exists(key)) throw new ConfigNotFoundException(key + " not found in config system");

        String str = getStr(key);
        Matcher matcher = numberPattern.matcher(str);
        if (!matcher.matches()) throw new ConfigValueFormatException(key + "'s value [" + str + "] is not a double");

        return Double.valueOf(matcher.group(1));
    }

    public int getInt(String key, int defaultValue) {
        String str = getStr(key);
        if (StringUtils.isEmpty(str)) return defaultValue;

        Matcher matcher = numberPattern.matcher(str);
        if (!matcher.matches()) return defaultValue;

        String intStr = StringUtils.substringBefore(matcher.group(1), ".");
        if (StringUtils.isEmpty(intStr)) return defaultValue;

        return Integer.valueOf(intStr);
    }

    public long getLong(String key, long defaultValue) {
        String str = getStr(key);
        if (StringUtils.isEmpty(str)) return defaultValue;

        Matcher matcher = numberPattern.matcher(str);
        if (!matcher.matches()) return defaultValue;

        String intStr = StringUtils.substringBefore(matcher.group(1), ".");
        if (StringUtils.isEmpty(intStr)) return defaultValue;

        return Long.valueOf(intStr);
    }

    public boolean getBool(String key, boolean defaultValue) {
        String str = getStr(key);
        if (StringUtils.isEmpty(str)) return defaultValue;

        return toBool(str);
    }

    private boolean toBool(String str) {
        return "true".equalsIgnoreCase(str) || "yes".equalsIgnoreCase(str)
                || "on".equalsIgnoreCase(str) || "y".equalsIgnoreCase(str);
    }

    public float getFloat(String key, float defaultValue) {
        String str = getStr(key);
        if (StringUtils.isEmpty(str)) return defaultValue;

        Matcher matcher = numberPattern.matcher(str);
        if (!matcher.matches()) return defaultValue;

        return Float.valueOf(matcher.group(1));
    }

    public double getDouble(String key, double defaultValue) {
        String str = getStr(key);
        if (StringUtils.isEmpty(str)) return defaultValue;

        Matcher matcher = numberPattern.matcher(str);
        if (!matcher.matches()) return defaultValue;

        return Double.valueOf(matcher.group(1));
    }

    public String getStr(String key, String defaultValue) {
        return StringUtils.defaultIfEmpty(getStr(key), defaultValue);
    }
    
    
    public List<String> getKeyPrefixes() {
        List<String> keyPrefixes = new ArrayList<String>();

        for (Object key : getProperties().keySet()) {
            String strKey = (String) key;

            String keyPrefix = StringUtils.substringBefore(strKey, ".");
            if (!keyPrefixes.contains(keyPrefix)) keyPrefixes.add(keyPrefix);
        }

        return keyPrefixes;
    }
}
