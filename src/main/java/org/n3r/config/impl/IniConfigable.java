package org.n3r.config.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

import org.n3r.config.ex.ConfigException;
import org.n3r.core.lang.RClose;
import org.springframework.core.io.Resource;

import com.google.common.base.Charsets;

public class IniConfigable extends DefaultConfigable {

    public IniConfigable(Resource res) {
        super(buildProperties(res));
    }

    private static Properties buildProperties(Resource res) {
        Reader reader = null;
        Properties props = new Properties();
        try {
            reader = new InputStreamReader(res.getInputStream(), Charsets.UTF_8);
            IniReader iniReader = new IniReader(reader);
            for (String section : iniReader.getSections()) {
                Properties sectionProps = iniReader.getSection(section);
                if (sectionProps == null) continue;

                String prefix = section.equals("") ? "" : section + '.';
                for (Map.Entry<Object, Object> entry : sectionProps.entrySet()) {
                    String key = prefix + entry.getKey();

                    if (!props.containsKey(key)) {
                        props.put(key, entry.getValue().toString());
                        continue;
                    }

                    throw new ConfigException(
                            "duplicate key in file " + res
                                    + " line " + iniReader.getLineNumber());
                }
            }
        }
        catch (IOException ex) {
            throw new ConfigException("read ini file error " + res, ex);
        }
        finally {
            RClose.closeQuietly(reader);
        }
        return props;
    }

}
