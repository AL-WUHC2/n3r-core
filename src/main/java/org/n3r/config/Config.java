package org.n3r.config;

import java.util.Properties;

import org.n3r.config.impl.ConfigBuilder;
import org.n3r.config.impl.IniConfigable;
import org.n3r.config.impl.PropsConfigable;
import org.n3r.core.lang.RClassPath;
import org.springframework.core.io.Resource;

public class Config {
    private static Configable configable;
    static {
        Resource envSpaceRes = RClassPath.getResource("envspace.props");
        String basePackage = "appconfig";
        ConfigBuilder configBuilder = new ConfigBuilder();
        if (envSpaceRes.exists()) {
            PropsConfigable envSpaceConfig = new PropsConfigable(envSpaceRes);
            basePackage = envSpaceConfig.getStr("envspacedir", basePackage);
            configBuilder.addConfig(envSpaceConfig);
        }

        Resource[] propsRes = RClassPath.getResources(basePackage, "**/*.props");
        for (Resource propRes : propsRes)
            configBuilder.addConfig(new PropsConfigable(propRes));
        Resource[] iniRes = RClassPath.getResources(basePackage, "**/*.ini");
        for (Resource propRes : iniRes)
            configBuilder.addConfig(new IniConfigable(propRes));

        configable = configBuilder.buildConfig();
    }

    public static boolean exists(String key) {
        return configable.exists(key);
    }

    public static Properties getProperties() {
        return configable.getProperties();
    }

    public static int getInt(String key) {
        return configable.getInt(key);
    }

    public static long getLong(String key) {
        return configable.getLong(key);
    }

    public static boolean getBool(String key) {
        return configable.getBool(key);
    }

    public static float getFloat(String key) {
        return configable.getFloat(key);
    }

    public static double getDouble(String key) {
        return configable.getDouble(key);
    }

    public static String getStr(String key) {
        return configable.getStr(key);
    }

    public static int getInt(String key, int defaultValue) {
        return configable.getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        return configable.getLong(key, defaultValue);
    }

    public static boolean getBool(String key, boolean defaultValue) {
        return configable.getBool(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        return configable.getFloat(key, defaultValue);
    }

    public static double getDouble(String key, double defaultValue) {
        return configable.getDouble(key, defaultValue);
    }

    public static String getStr(String key, String defaultValue) {
        return configable.getStr(key, defaultValue);
    }

    public static Configable subset(String prefix) {
        return configable.subset(prefix);
    }

}
