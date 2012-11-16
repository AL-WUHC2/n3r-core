package org.n3r.core.xml;

import org.n3r.core.xml.marshal.RMarshaller;
import org.n3r.core.xml.unmarshal.RUnmarshaller;
import org.n3r.core.xmltool.XMLTag;

import static org.n3r.core.lang.RStr.*;
import static org.n3r.core.xml.utils.RXmlFormatter.*;
import static org.n3r.core.xmltool.XMLDoc.*;

public class RXml {

    public static String beanToXml(Object obj) {
        return beanToXml(obj, obj.getClass().getSimpleName());
    }

    public static String beanToXml(Object obj, String rootName) {
        XMLTag result = null;
        return formatXml(new RMarshaller().marshal(rootName, obj, result));
    }

    public static String beanToXmlWithFormat(Object obj) {
        return beanToXmlWithFormat(obj, obj.getClass().getSimpleName());
    }

    public static String beanToXmlWithFormat(Object obj, String rootName) {
        XMLTag result = null;
        return toStr(new RMarshaller().marshal(rootName, obj, result));
    }

    public static <T> T xmlToBean(String xml, Class<T> clazz) {
        return new RUnmarshaller().unmarshal(from(xml, false), clazz);
    }

}
