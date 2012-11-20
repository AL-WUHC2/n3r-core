package org.n3r.core.xml.marshal;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.n3r.core.xml.FieldsTraverser;
import org.n3r.core.xml.RXml;
import org.n3r.core.xml.XMarshalAware;
import org.n3r.core.xml.annotation.RXCData;
import org.n3r.core.xml.annotation.RXElement;
import org.n3r.core.xml.annotation.RXSkip;
import org.n3r.core.xml.utils.RXSkipWhen;
import org.n3r.core.xmltool.XMLTag;

import static org.apache.commons.lang3.StringUtils.*;
import static org.n3r.core.lang.RClass.*;
import static org.n3r.core.lang.RField.*;
import static org.n3r.core.xml.utils.RJaxbClassesScanner.*;
import static org.n3r.core.xmltool.XMLDoc.*;

public class RMarshaller extends FieldsTraverser implements XMarshalAware {

    private XMLTag currentTag;
    private Object marsharlObject;

    public XMLTag marshal(String tagName, Object object, XMLTag parent) {
        marsharlObject = object;

        XMarshalAware marshaller = getMarshaller(marsharlObject.getClass());
        if (marshaller != null) return marshaller.marshal(tagName, marsharlObject, parent);

        currentTag = parent == null ? newDocument(false).addRoot(tagName) : parent.addTag(tagName);

        traverseFields(marsharlObject.getClass());

        return parent == null ? currentTag : currentTag.gotoParent();
    }

    @Override
    public void processFields(PropertyDescriptor pDescriptor) throws Exception {
        Method method = pDescriptor.getReadMethod();
        if (method == null) return;

        String fieldName = pDescriptor.getName();
        Field field = getTraverseDeclaredField(marsharlObject.getClass(), fieldName);
        RXSkip rxSkip = field.getAnnotation(RXSkip.class);
        if (rxSkip != null && rxSkip.value() == RXSkipWhen.Absolute) return;
        if (isNotNormal(field)) return;

        Object fieldValue = method.invoke(marsharlObject);
        if (rxSkip != null && rxSkip.value() == RXSkipWhen.Null && fieldValue == null) return;

        Class<?> unmarType = field.getType();
        if (isAssignable(unmarType, List.class)) {
            // remove last 's'
            fieldName = fieldName.substring(0, fieldName.length() - 1);
        }

        RXElement element = field.getAnnotation(RXElement.class);
        String elementName = element == null ? capitalize(fieldName) : element.value();

        if (field.isAnnotationPresent(RXCData.class)) {
            currentTag.addTag(elementName).addCDATA(RXml.beanToXml(fieldValue)).gotoParent();
            return;
        }

        XMarshalAware marshaller = getMarshaller(unmarType);
        marshaller = marshaller != null ? marshaller : new RMarshaller();

        marshaller.marshal(elementName, fieldValue, currentTag);
    }

}
