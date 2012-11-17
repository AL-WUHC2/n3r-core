package org.n3r.core.xml.unmarshal;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.n3r.core.xml.FieldsTraverser;
import org.n3r.core.xml.UnmarshalAware;
import org.n3r.core.xml.annotation.RXElement;
import org.n3r.core.xml.annotation.RXTransient;
import org.n3r.core.xmltool.XMLTag;

import static org.apache.commons.lang3.StringUtils.*;
import static org.n3r.core.joor.Reflect.*;
import static org.n3r.core.lang.RClass.*;
import static org.n3r.core.lang.RField.*;
import static org.n3r.core.lang.RType.*;
import static org.n3r.core.xml.utils.RJaxbClassesScanner.*;

public class RUnmarshaller<T> extends FieldsTraverser implements UnmarshalAware<T> {

    private XMLTag currentTag;
    private Class<?> unmarsharlClazz;
    private T unmarshalObject;

    @SuppressWarnings("unchecked")
    @Override
    public T unmarshal(XMLTag xmlNode, Class<?> clazz) {
        currentTag = xmlNode;
        unmarsharlClazz = clazz;

        UnmarshalAware<?> unmarshaller = getUnmarshaller(unmarsharlClazz);
        if (unmarshaller != null) return (T) unmarshaller.unmarshal(currentTag, unmarsharlClazz);

        unmarshalObject = on(unmarsharlClazz).create().get();
        traverseFields(unmarsharlClazz);

        return unmarshalObject;
    }

    @Override
    public void processFields(PropertyDescriptor pDescriptor) throws Exception {
        Method method = pDescriptor.getWriteMethod();
        if (method == null) return;

        String fieldName = pDescriptor.getName();
        Field field = getTraverseDeclaredField(unmarsharlClazz, fieldName);

        if (field.isAnnotationPresent(RXTransient.class)) return;
        if (isNotNormal(field)) return;

        Class<?> unmarType = field.getType();
        if (isAssignable(unmarType, List.class)) {
            unmarType = getActualTypeArgument(field.getGenericType());
            if (unmarType == Void.class) throw new RuntimeException("Unkown List Item Class for " + fieldName);
            fieldName = fieldName.substring(0, fieldName.length() - 1); // List then remove "s"
        }

        RXElement element = field.getAnnotation(RXElement.class);
        String fieldTagName = element == null ? capitalize(fieldName) : element.value();

        UnmarshalAware<?> unmarshaller = getUnmarshaller(field.getType());
        unmarshaller = unmarshaller == null ? new RUnmarshaller<Object>() : unmarshaller;

        method.invoke(unmarshalObject,
                unmarshaller.unmarshal(currentTag.gotoFirstChild(fieldTagName), unmarType));
        currentTag.gotoParent();
    }
}
