package org.n3r.eson.mapper;


public interface EsonSerializable {
    void write(EsonSerializer serializer, Object object);
}
