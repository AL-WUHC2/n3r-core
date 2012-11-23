package org.n3r.core.xml.unmarshal;

import org.n3r.core.xml.RXml;
import org.n3r.core.xml.impl.BaseUnmarshaller;
import org.n3r.core.xmltool.XMLTag;

public class CDataUnmarshaller extends BaseUnmarshaller {

    @Override
    public Object unmarshal(XMLTag xmlNode, Class<?> clazz) {
        Class<?> actualClass = getActualClass(xmlNode, clazz);
        if (actualClass.equals(Void.class) && typeMapping == null) return null;

        return RXml.xmlToBean(xmlNode.getCDATA(), actualClass, typeMapping);
    }

}
