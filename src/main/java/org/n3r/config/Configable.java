package org.n3r.config;

import java.util.List;
import java.util.Properties;

/**
 * 读取配置.
 * @author Bingoo
 *
 */
public interface Configable {
    boolean exists(String key);

    Properties getProperties();

    int getInt(String key);

    long getLong(String key);

    boolean getBool(String key);

    float getFloat(String key);

    double getDouble(String key);

    String getStr(String key);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    boolean getBool(String key, boolean defaultValue);

    float getFloat(String key, float defaultValue);

    double getDouble(String key, double defaultValue);

    String getStr(String key, String defaultValue);

    Configable subset(String prefix);

    List<String> getKeyPrefixes();
}
