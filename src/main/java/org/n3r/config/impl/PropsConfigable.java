package org.n3r.config.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.n3r.config.ex.ConfigException;
import org.n3r.core.lang.RClose;
import org.springframework.core.io.Resource;

import com.google.common.base.Charsets;

public class PropsConfigable extends DefaultConfigable {
    public PropsConfigable(Resource res) {
       super(buildProperties(res));
    }

    private static Properties buildProperties(Resource res) {
        PropsReader reader = null;
        Properties props = new Properties();
        try {
            reader = new PropsReader(new InputStreamReader(res.getInputStream(), Charsets.UTF_8));
            while (reader.nextProperty()) {
                if (!props.containsKey(reader.getPropertyName())) {
                    props.put(reader.getPropertyName(), reader.getPropertyValue());
                    continue;
                }

                throw new ConfigException(
                        "duplicate key in file " + res.getDescription() + " line " + reader.getLineNumber());
            }

            return props;
        }
        catch (IOException ex) {
            throw new ConfigException("read props file error " + res.getDescription(), ex);
        }
        finally {
            RClose.closeQuietly(reader);
        }
    }

}
