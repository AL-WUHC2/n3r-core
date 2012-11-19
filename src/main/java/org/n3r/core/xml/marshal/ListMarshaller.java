package org.n3r.core.xml.marshal;

import java.util.List;

import org.n3r.core.xml.XMarshalAware;
import org.n3r.core.xml.annotation.RXBindTo;
import org.n3r.core.xmltool.XMLTag;

@RXBindTo(List.class)
public class ListMarshaller implements XMarshalAware {

    @Override
    public XMLTag marshal(String tagName, Object object, XMLTag parent, boolean isCData) {
        if (object == null) return parent;
        String itemTagName = tagName.substring(0, tagName.length() - 1);
        for (Object item : (List<?>) object) {
            new RMarshaller().marshal(itemTagName, item, parent, isCData);
        }
        return parent;
    }

}
