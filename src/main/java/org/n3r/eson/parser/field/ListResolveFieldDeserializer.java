package org.n3r.eson.parser.field;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.n3r.eson.JSONArray;
import org.n3r.eson.parser.FieldDeserializer;
import org.n3r.eson.parser.JSONParser;
import org.n3r.eson.utils.TypeUtils;

@SuppressWarnings("rawtypes")
public final class ListResolveFieldDeserializer extends FieldDeserializer {

    private final int index;
    private final List list;
    private final JSONParser parser;

    public ListResolveFieldDeserializer(JSONParser parser, List list, int index) {
        super(null, null);
        this.parser = parser;
        this.index = index;
        this.list = list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Object object, Object value) {
        list.set(index, value);

        if (list instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) list;
            Object array = jsonArray.getRelatedArray();

            if (array != null) {
                int arrayLength = Array.getLength(array);

                if (arrayLength > index) {
                    Object item;
                    if (jsonArray.getComponentType() != null) item = TypeUtils.cast(value,
                            jsonArray.getComponentType(), parser.getConfig());
                    else item = value;
                    Array.set(array, index, item);
                }
            }
        }
    }

    public JSONParser getParser() {
        return parser;
    }

    @Override
    public void parseField(JSONParser parser, Object object, Type objectType, Map<String, Object> fieldValues) {

    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
