package org.n3r.eson;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.n3r.eson.parser.FieldInfo;
import org.n3r.eson.parser.ParserMapping;
import org.n3r.eson.utils.TypeUtils;

public abstract class JSON {

    @SuppressWarnings("unchecked")
    public static final Object toJSON(Object javaObjectx) {
        return toJSON(javaObjectx, ParserMapping.getGlobalInstance());
    }

    @SuppressWarnings("unchecked")
    public static final Object toJSON(Object javaObject, ParserMapping mapping) {
        if (javaObject == null) return null;

        if (javaObject instanceof JSON) return javaObject;

        if (javaObject instanceof Map) {

            Map<Object, Object> map = (Map<Object, Object>) javaObject;

            JSONObject json = new JSONObject(map.size());

            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                Object key = entry.getKey();
                String jsonKey = TypeUtils.castToString(key);
                Object jsonValue = toJSON(entry.getValue());
                json.put(jsonKey, jsonValue);
            }

            return json;
        }

        if (javaObject instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) javaObject;

            JSONArray array = new JSONArray(collection.size());

            for (Object item : collection) {
                Object jsonValue = toJSON(item);
                array.add(jsonValue);
            }

            return array;
        }

        Class<?> clazz = javaObject.getClass();

        if (clazz.isEnum()) return ((Enum<?>) javaObject).name();

        if (clazz.isArray()) {
            int len = Array.getLength(javaObject);

            JSONArray array = new JSONArray(len);

            for (int i = 0; i < len; ++i) {
                Object item = Array.get(javaObject, i);
                Object jsonValue = toJSON(item);
                array.add(jsonValue);
            }

            return array;
        }

        if (mapping.isPrimitive(clazz)) return javaObject;

        try {
            List<FieldInfo> getters = TypeUtils.computeGetters(clazz, null);

            JSONObject json = new JSONObject(getters.size());

            for (FieldInfo field : getters) {
                Object value = field.get(javaObject);
                Object jsonValue = toJSON(value);

                json.put(field.getName(), jsonValue);
            }

            return json;
        } catch (Exception e) {
            throw new EsonException("toJSON error", e);
        }
    }

    @Override
    public String toString() {
        return new Eson().toString(this);
    }
}
