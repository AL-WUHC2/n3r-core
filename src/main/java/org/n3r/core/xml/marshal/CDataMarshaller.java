package org.n3r.core.xml.marshal;

import org.n3r.core.xml.RXml;
import org.n3r.core.xml.impl.BaseMarshaller;
import org.n3r.core.xmltool.XMLTag;

public class CDataMarshaller extends BaseMarshaller {

    @Override
    public XMLTag marshal(String tagName, Object object, XMLTag parent) {
        parent = buildCurrentTag(tagName, parent);
        writeObjectClass(object.getClass(), parent);

        return parent.addCDATA(RXml.beanToXml(object));
    }

}
