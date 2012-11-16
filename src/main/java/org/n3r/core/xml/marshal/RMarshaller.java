package org.n3r.core.xml.marshal;

import java.lang.reflect.Field;

import org.n3r.core.lang.RField;
import org.n3r.core.xml.XMarshalAware;
import org.n3r.core.xml.annotation.RXTransient;
import org.n3r.core.xmltool.XMLTag;

import static org.apache.commons.lang3.StringUtils.*;
import static org.n3r.core.joor.Reflect.*;
import static org.n3r.core.xml.utils.RJaxbClassesScanner.*;
import static org.n3r.core.xmltool.XMLDoc.*;

public class RMarshaller implements XMarshalAware {

    public XMLTag marshal(String tagName, Object object, XMLTag parent) {
        XMarshalAware marshaller = getMarshaller(object.getClass());
        if (marshaller != null) return marshaller.marshal(tagName, object, parent);

        XMLTag result = parent == null ? newDocument(false).addRoot(tagName) : parent.addTag(tagName);

        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(RXTransient.class)) continue;
            if (RField.isNotNormal(field)) continue;

            String fieldName = capitalize(field.getName());
            Object fieldValue = on(object).get(field.getName());

            marshaller = getMarshaller(field.getType());

            marshaller = marshaller != null ? marshaller : new RMarshaller();
            marshaller.marshal(fieldName, fieldValue, result);
        }
        return parent == null ? result : result.gotoParent();
    }

}
