package org.n3r.config.impl;

import java.util.Properties;

import org.n3r.config.Configable;

public class ConfigBuilder {
    Properties properties = new Properties();

    public void addConfig(Configable config) {
        properties.putAll(config.getProperties());
    }

    public Configable buildConfig() {
        return new DefaultConfigable(properties);
    }

}
