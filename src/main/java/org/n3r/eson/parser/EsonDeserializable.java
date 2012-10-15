package org.n3r.eson.parser;

import java.lang.reflect.Type;

public interface EsonDeserializable {
    Object deserialize(JSONParser parser, Type type, Object fieldName);

    int getFastMatchToken();
}
