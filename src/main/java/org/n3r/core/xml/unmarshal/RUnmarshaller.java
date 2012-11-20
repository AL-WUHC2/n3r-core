package org.n3r.core.xml.unmarshal;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.n3r.core.xml.FieldsTraverser;
import org.n3r.core.xml.RXml;
import org.n3r.core.xml.XUnmarshalAware;
import org.n3r.core.xml.annotation.RXCData;
import org.n3r.core.xml.annotation.RXElement;
import org.n3r.core.xml.annotation.RXSkip;
import org.n3r.core.xml.utils.RXSkipWhen;
import org.n3r.core.xmltool.XMLTag;

import static org.apache.commons.lang3.StringUtils.*;
import static org.n3r.core.joor.Reflect.*;
import static org.n3r.core.lang.RClass.*;
import static org.n3r.core.lang.RField.*;
import static org.n3r.core.lang.RType.*;
import static org.n3r.core.xml.utils.RJaxbClassesScanner.*;

public class RUnmarshaller<T> extends FieldsTraverser implements XUnmarshalAware<T> {

    private XMLTag currentTag;
    private Class<?> unmarsharlClazz;
    private T unmarshalObject;

    @SuppressWarnings("unchecked")
    @Override
    public T unmarshal(XMLTag xmlNode, Class<?> clazz) {
        currentTag = xmlNode;
        unmarsharlClazz = clazz;

        XUnmarshalAware<?> unmarshaller = getUnmarshaller(unmarsharlClazz);
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

        RXSkip rxSkip = field.getAnnotation(RXSkip.class);
        if (rxSkip != null && rxSkip.value() == RXSkipWhen.Absolute) return;
        if (isNotNormal(field)) return;

        Class<?> unmarType = field.getType();
        if (isAssignable(unmarType, List.class)) {
            unmarType = getActualTypeArgument(field.getGenericType());
            if (unmarType == Void.class) throw new RuntimeException("Unkown List Item Class for " + fieldName);
            fieldName = fieldName.substring(0, fieldName.length() - 1); // List then remove "s"
        }

        RXElement element = field.getAnnotation(RXElement.class);
        String fieldTagName = element == null ? capitalize(fieldName) : element.value();

        XMLTag child = null;
        try {
            child = currentTag.gotoFirstChild(fieldTagName);
        }
        catch (Exception e) {
            if (rxSkip != null && rxSkip.value() == RXSkipWhen.Null) return;
            if (isAssignable(field.getType(), List.class)) return;
            throw new RuntimeException("Node " + fieldTagName + " isn't found in " + currentTag.getCurrentTagName());
        }

        Object fieldValue = null;
        if (field.isAnnotationPresent(RXCData.class)) {
            fieldValue = RXml.xmlToBean(child.getCDATA(), field.getType());
        }
        else {
            XUnmarshalAware<?> unmarshaller = getUnmarshaller(field.getType());
            unmarshaller = unmarshaller == null ? new RUnmarshaller<Object>() : unmarshaller;
            fieldValue = unmarshaller.unmarshal(child, unmarType);
        }

        method.invoke(unmarshalObject, fieldValue);
        currentTag.gotoParent();
    }
}
