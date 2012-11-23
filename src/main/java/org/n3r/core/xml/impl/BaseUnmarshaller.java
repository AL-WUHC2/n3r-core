package org.n3r.core.xml.impl;

import java.util.Map;

import org.n3r.core.xml.FieldsTraverser;
import org.n3r.core.xml.XUnmarshalAware;
import org.n3r.core.xmltool.XMLTag;

import static org.n3r.core.joor.Reflect.*;
import static org.n3r.core.lang.RClass.*;

public abstract class BaseUnmarshaller extends FieldsTraverser implements XUnmarshalAware {

    protected Object typeMapping = null;

    protected Class<?> getActualClass(XMLTag tag, Class<?> clazz) {
        return typeMapping != null && isAssignable(typeMapping.getClass(), Class.class) ? (Class<?>) typeMapping :
                tag.hasAttribute("_type_") ? on(tag.getAttribute("_type_")).type() : clazz;
    }

    @SuppressWarnings("unchecked")
    protected Object getFieldType(String name) {
        if (typeMapping == null || !isAssignable(typeMapping.getClass(), Map.class)) return null;
        return ((Map) typeMapping).get(name);
    }

    public BaseUnmarshaller setTypeMapping(Object typeMapping) {
        this.typeMapping = typeMapping;
        return this;
    }
}
