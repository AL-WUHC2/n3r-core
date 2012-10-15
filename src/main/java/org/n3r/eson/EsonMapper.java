package org.n3r.eson;

import java.lang.reflect.Type;

import org.n3r.eson.mapper.EsonSerializable;
import org.n3r.eson.parser.EsonDeserializable;
import org.n3r.eson.utils.IdentityHashMap;

public class EsonMapper {
    private IdentityHashMap<Type, EsonSerializable> serializerMapping = new IdentityHashMap<Type, EsonSerializable>();
    private IdentityHashMap<Type, EsonDeserializable> deserializerMapping = new IdentityHashMap<Type, EsonDeserializable>();

    public void put(Type type, EsonSerializable serializer) {
        serializerMapping.put(type, serializer);
    }

    public void put(Type type, EsonDeserializable deserializer) {
        deserializerMapping.put(type, deserializer);
    }

    public EsonSerializable getSerializer(Type type) {
        return serializerMapping.get(type);
    }

    public EsonDeserializable getDeserializer(Type type) {
        return deserializerMapping.get(type);
    }
}
