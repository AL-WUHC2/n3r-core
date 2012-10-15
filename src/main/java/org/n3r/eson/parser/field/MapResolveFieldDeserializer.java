package org.n3r.eson.parser.field;

import java.lang.reflect.Type;
import java.util.Map;

import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.JSONParser;

@SuppressWarnings("rawtypes")
public final class MapResolveFieldDeserializer extends FieldDeserializer {

    private final String key;
    private final Map map;

    public MapResolveFieldDeserializer(Map map, String index) {
        super(null, null);
        key = index;
        this.map = map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object object, Object value) {
        map.put(key, value);
    }

    @Override
    public void parseField(JSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {

    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
