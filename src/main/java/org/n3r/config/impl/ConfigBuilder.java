package org.n3r.config.impl;

import java.util.Properties;

import org.n3r.config.Configable;

public class ConfigBuilder {
    private Properties properties;
    public void setDefConfig(Configable defConfig) {
        properties = new Properties(defConfig != null ? defConfig.getProperties() : null);
    }

    public void addConfig(Configable config) {
        properties.putAll(config.getProperties());
    }

    public Configable buildConfig() {
        return new DefaultConfigable(properties);
    }
}
