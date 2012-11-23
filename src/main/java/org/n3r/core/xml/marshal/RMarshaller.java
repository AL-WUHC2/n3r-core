package org.n3r.core.xml.marshal;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.n3r.core.xml.annotation.RXCData;
import org.n3r.core.xml.annotation.RXElement;
import org.n3r.core.xml.annotation.RXSkip;
import org.n3r.core.xml.impl.BaseMarshaller;
import org.n3r.core.xml.utils.RXFeature;
import org.n3r.core.xml.utils.RXSkipWhen;
import org.n3r.core.xmltool.XMLTag;

import static org.apache.commons.lang3.StringUtils.*;
import static org.n3r.core.lang.RClass.*;
import static org.n3r.core.lang.RField.*;
import static org.n3r.core.lang.RType.*;
import static org.n3r.core.xml.utils.RXmlClassesScanner.*;

public class RMarshaller extends BaseMarshaller {

    private Object currObject;
    private XMLTag currTag;

    @Override
    public XMLTag marshal(String tagName, Object object, XMLTag parent) {
        Class<?> objClass = object.getClass();
        BaseMarshaller marshaller = getMarshaller(objClass);
        if (marshaller != null) return marshaller.addFeatures(features).marshal(tagName, object, parent);

        currObject = object;
        currTag = buildCurrentTag(tagName, parent);
        writeObjectClass(objClass, currTag);

        traverseFields(objClass);

        return parent == null ? currTag : currTag.gotoParent();
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

        // 不可读则跳过
        Method method = pDescriptor.getReadMethod();
        if (method == null) return;

        // 标注: 为空则跳过
        Object fieldValue = method.invoke(currObject);
        if (rxSkip != null && rxSkip.value() == RXSkipWhen.Null && fieldValue == null) return;

        // 特殊处理: List字段=>字段名去除最后一个字符 : 非List字段=>为空异常
        if (isAssignable(field.getType(), List.class)) fieldName = fieldName.substring(0, fieldName.length() - 1); // remove last 's'
        else if (fieldValue == null) throw new RuntimeException("Field " + fieldName + " can't be Null");

        // 字段映射标签名
        RXElement element = field.getAnnotation(RXElement.class);
        String elementName = element == null ? capitalize(fieldName) : element.value();

        // BaseMarshaller特性参数: 泛型字段||未明确泛型的List字段=>记录类型属性
        Type genericType = field.getGenericType();
        List<RXFeature> fParams = isAssignable(genericType.getClass(), TypeVariable.class) ||
                isAssignable(field.getType(), List.class) && getActualTypeArgument(genericType).equals(Void.class) ?
                Arrays.asList(RXFeature.WrithObjectClass) : new ArrayList<RXFeature>();

        // CDATA字段
        if (field.isAnnotationPresent(RXCData.class)) {
            new CDataMarshaller().addFeatures(fParams).marshal(elementName, fieldValue, currTag);
            return;
        }

        // 一般字段
        BaseMarshaller marshaller = getMarshaller(fieldValue == null ? field.getType() : fieldValue.getClass());
        if (marshaller == null) marshaller = new RMarshaller();

        marshaller.addFeatures(fParams).marshal(elementName, fieldValue, currTag);
    }
}
