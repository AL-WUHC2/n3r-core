package org.n3r.eson.mapper.serializer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.n3r.eson.EsonFeature;
import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.mapper.EsonSerializer;
import org.n3r.eson.mapper.SerializeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapSerializer implements EsonSerializable {
    private Logger log = LoggerFactory.getLogger(MapSerializer.class);
    public static MapSerializer instance = new MapSerializer();

    @Override
    public void write(EsonSerializer serializer, Object object) {
        SerializeWriter out = serializer.getWriter();

        if (object == null) out.writeNull();
        else {
            Map<?, ?> map = sortMap(object, out);
            serializer.setUsePrettyFormat(map.size() > 0);

            serializer.printlnWhenIdent();
            out.write('{');

            serializer.incrementIndent();
            writeMap(serializer, out, map);
            serializer.decrementIdentAndPrintln();

            out.write('}');
        }
    }

    private void writeMap(EsonSerializer serializer, SerializeWriter out, Map<?, ?> map) {

        boolean first = true;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) out.write(',');
            else first = false;
            serializer.println();

            Object entryKey = writeKey(serializer, out, entry);
            writeValue(serializer, out, entry, entryKey);
        }
    }

    private void writeValue(EsonSerializer serializer, SerializeWriter out, Map.Entry<?, ?> entry, Object entryKey) {
        Object value = entry.getValue();
        if (value == null) {
            out.writeNull();
            return;
        }

        EsonSerializable valueSerializer = serializer.getSerializer(value.getClass());
        valueSerializer.write(serializer, value);
    }

    protected Map<?, ?> sortMap(Object object, SerializeWriter out) {
        Map<?, ?> map = (Map<?, ?>) object;

        if (out.isEnabled(EsonFeature.SortKey))
            if (!(map instanceof SortedMap) && !(map instanceof LinkedHashMap)) try {
                map = new TreeMap<Object, Object>(map);
            } catch (Exception ex) {
                log.warn("new TreeMap failed", ex);
            }

        return map;
    }

    private Object writeKey(EsonSerializer serializer, SerializeWriter out, Map.Entry<?, ?> entry) {
        Object entryKey = entry.getKey();

        if (entryKey == null || entryKey instanceof String) out.writeKey((String) entryKey);
        else {
            serializer.write(entryKey);
            out.write(':');
        }

        return entryKey;
    }
}
