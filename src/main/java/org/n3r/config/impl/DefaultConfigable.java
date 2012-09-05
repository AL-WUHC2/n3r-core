package org.n3r.config.impl;

import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.n3r.config.Configable;
import org.n3r.core.security.AesCryptor;

public class DefaultConfigable extends BaseConfigable {
    private Properties properties;

    public DefaultConfigable(Properties properties) {
        this.properties = properties;
    }

    public boolean exists(String key) {
        return properties.containsKey(key);
    }

    public Properties getProperties() {
        return properties;
    }

    public static void main(String[] args) {
        System.out.println(new AesCryptor("n3rconfigkey").encrypt("orcl"));
    }
    public String getStr(String key) {
        String property = properties.getProperty(key);
        property = StrSubstitutor.replace(property, properties);
        if (StringUtils.startsWith(property, "{AES}")) 
            property = new AesCryptor("n3rconfigkey").decrypt(property.substring(5));

        return StringUtils.trim(property);
    }

    public Configable subset(String prefix) {
        Properties subProps = new Properties();

        if (StringUtils.isEmpty(prefix)) return new DefaultConfigable(subProps);

        String prefixMatch = prefix.charAt(prefix.length() - 1) != '.' ? prefix + '.' : prefix;

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();

            if (!key.startsWith(prefixMatch)) continue;

            subProps.put(key.substring(prefixMatch.length()), entry.getValue());
        }

        return new DefaultConfigable(subProps);
    }

}
