package org.n3r.core.xml.unmarshal;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

import org.n3r.core.xml.annotation.RXCData;
import org.n3r.core.xml.annotation.RXElement;
import org.n3r.core.xml.annotation.RXSkip;
import org.n3r.core.xml.impl.BaseUnmarshaller;
import org.n3r.core.xml.utils.RXSkipWhen;
import org.n3r.core.xmltool.XMLDocumentException;
import org.n3r.core.xmltool.XMLTag;

import static org.apache.commons.lang3.StringUtils.*;
import static org.n3r.core.joor.Reflect.*;
import static org.n3r.core.lang.RClass.*;
import static org.n3r.core.lang.RField.*;
import static org.n3r.core.lang.RType.*;
import static org.n3r.core.xml.utils.RXmlClassesScanner.*;

public class RUnmarshaller extends BaseUnmarshaller {

    private Object currObject;
    private XMLTag currTag;

    @Override
    public Object unmarshal(XMLTag xmlNode, Class<?> clazz) {
        Class<?> actualClass = getActualClass(xmlNode, clazz);
        if (actualClass.equals(Void.class)) return null;

        BaseUnmarshaller unmarshaller = getUnmarshaller(actualClass);
        if (unmarshaller != null) return unmarshaller.setTypeMapping(typeMapping).unmarshal(xmlNode, actualClass);

        currTag = xmlNode;

        currObject = on(actualClass).create().get();
        traverseFields(actualClass);

        return currObject;
    }

    @Override
    public void processFields(PropertyDescriptor pDescriptor) throws Exception {
        // 无此字段则跳过
        String fieldName = pDescriptor.getName();
        Field field = getTraverseDeclaredField(currObject.getClass(), fieldName);
        if (field == null) return;

        // 标注跳过
        RXSkip rxSkip = field.getAnnotation(RXSkip.class);
        if (rxSkip != null && rxSkip.value() == RXSkipWhen.Absolute) return;
        if (isNotNormal(field)) return;

        // 不可写则跳过
        Method method = pDescriptor.getWriteMethod();
        if (method == null) return;

        // 字段映射标签名
        if (isAssignable(field.getType(), List.class)) fieldName = fieldName.substring(0, fieldName.length() - 1); // List then remove "s"
        RXElement element = field.getAnnotation(RXElement.class);
        String fieldTagName = element == null ? capitalize(fieldName) : element.value();

        // 获取对应标签
        XMLTag child = null;
        try {
            child = currTag.gotoFirstChild(fieldTagName);
        }
        catch (XMLDocumentException e) {
            // 为空跳过
            if (rxSkip != null && rxSkip.value() == RXSkipWhen.Null) return;
            // 空List跳过
            if (isAssignable(field.getType(), List.class)) return;
            throw new RuntimeException("Node " + fieldTagName + " isn't found in " + currTag.getCurrentTagName());
        }

        // 取得类型: List字段取得实际类型orVoid||非泛型字段取得类型||泛型字段=>Void
        Type genericType = field.getGenericType();
        Class<?> actualClass = isAssignable(field.getType(), List.class) ? getActualTypeArgument(genericType) :
                !isAssignable(genericType.getClass(), TypeVariable.class) ? field.getType() : Void.class;

        BaseUnmarshaller unmarshaller = field.isAnnotationPresent(RXCData.class) ? new CDataUnmarshaller() :
                    getUnmarshaller(field.getType());
        if (unmarshaller == null) unmarshaller = new RUnmarshaller();

        method.invoke(currObject, unmarshaller.setTypeMapping(getFieldType(pDescriptor.getName())).unmarshal(child,
                actualClass));
        currTag.gotoParent();
    }
}
