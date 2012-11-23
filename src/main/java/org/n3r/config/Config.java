package org.n3r.config;

import java.util.Properties;

import org.n3r.config.impl.ConfigBuilder;
import org.n3r.config.impl.IniConfigable;
import org.n3r.config.impl.PropsConfigable;
import org.n3r.core.lang.RClassPath;
import org.springframework.core.io.Resource;

public class Config {
    private static Configable bizConfig;
    static {
        Configable defConfig = createConfigable("defconfigdir", "defconfig", null);
        bizConfig = createConfigable("bizconfigdir", "bizconfig", defConfig);
    }

    private static Configable createConfigable(String bizConfigDir, String defConfigDir,Configable defConfig ) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.setDefConfig(defConfig);

        String basePackage = defConfigDir;
        Resource envSpaceRes = RClassPath.getResource("envspace.props");
        if (envSpaceRes.exists()) {
            PropsConfigable envSpaceConfig = new PropsConfigable(envSpaceRes);
            basePackage = envSpaceConfig.getStr(bizConfigDir, defConfigDir);
            configBuilder.addConfig(envSpaceConfig);
        }

        Resource[] propsRes = RClassPath.getResources(basePackage, "**/*.props");
        for (Resource propRes : propsRes)
            configBuilder.addConfig(new PropsConfigable(propRes));
        Resource[] iniRes = RClassPath.getResources(basePackage, "**/*.ini");
        for (Resource propRes : iniRes)
            configBuilder.addConfig(new IniConfigable(propRes));

       return configBuilder.buildConfig();
    }

    public static boolean exists(String key) {
        return bizConfig.exists(key) ;
    }

    public static Properties getProperties() {
        return bizConfig.getProperties();
    }

    public static int getInt(String key) {
        return bizConfig.getInt(key);
    }

    public static long getLong(String key) {
        return bizConfig.getLong(key);
    }

    public static boolean getBool(String key) {
        return bizConfig.getBool(key);
    }

    public static float getFloat(String key) {
        return bizConfig.getFloat(key);
    }

    public static double getDouble(String key) {
        return bizConfig.getDouble(key);
    }

    public static String getStr(String key) {
        return bizConfig.getStr(key);
    }

    public static int getInt(String key, int defaultValue) {
        return bizConfig.getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        return bizConfig.getLong(key, defaultValue);
    }

    public static boolean getBool(String key, boolean defaultValue) {
        return bizConfig.getBool(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        return bizConfig.getFloat(key, defaultValue);
    }

    public static double getDouble(String key, double defaultValue) {
        return bizConfig.getDouble(key, defaultValue);
    }

    public static String getStr(String key, String defaultValue) {
        return bizConfig.getStr(key, defaultValue);
    }

    public static Configable subset(String prefix) {
        return bizConfig.subset(prefix);
    }

}
