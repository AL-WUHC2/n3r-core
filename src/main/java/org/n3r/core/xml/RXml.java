package org.n3r.core.xml;

import org.n3r.core.xml.annotation.RXRootElement;
import org.n3r.core.xml.marshal.RMarshaller;
import org.n3r.core.xml.unmarshal.RUnmarshaller;
import org.n3r.core.xmltool.XMLTag;

import static org.n3r.core.lang.RStr.*;
import static org.n3r.core.xml.utils.RXmlFormatter.*;
import static org.n3r.core.xmltool.XMLDoc.*;

public class RXml {

    public static String beanToXml(Object obj) {
        return beanToXml(obj, false);
    }

    public static String beanToXml(Object obj, boolean enableFeature) {
        return beanToXml(obj, getRootName(obj), enableFeature);
    }

    public static String beanToXml(Object obj, String rootName, boolean enableFeature) {
        XMLTag result = null;
        return formatXml(new RMarshaller().setEnableFeature(enableFeature).marshal(rootName, obj, result));
    }

    public static String beanToXmlWithFormat(Object obj) {
        return beanToXmlWithFormat(obj, false);
    }

    public static String beanToXmlWithFormat(Object obj, boolean enableFeature) {
        return beanToXmlWithFormat(obj, getRootName(obj), enableFeature);
    }

    public static String beanToXmlWithFormat(Object obj, String rootName, boolean enableFeature) {
        XMLTag result = null;
        return toStr(new RMarshaller().setEnableFeature(enableFeature).marshal(rootName, obj, result));
    }

    public static <T> T xmlToBean(String xml, Class<T> clazz) {
        return xmlToBean(xml, clazz, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T xmlToBean(String xml, Class<T> clazz, Object typeMapping) {
        return (T) new RUnmarshaller().setTypeMapping(typeMapping).unmarshal(from(xml, false), clazz);
    }

    private static String getRootName(Object obj) {
        Class<?> clazz = obj.getClass();
        RXRootElement rootElement = clazz.getAnnotation(RXRootElement.class);
        return rootElement == null ? clazz.getSimpleName() : rootElement.value();
    }

}
