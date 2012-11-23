package org.n3r.core.xml.marshal;

import java.util.List;

import org.n3r.core.xml.annotation.RXBindTo;
import org.n3r.core.xml.impl.BaseMarshaller;
import org.n3r.core.xmltool.XMLTag;

@RXBindTo(List.class)
public class ListMarshaller extends BaseMarshaller {

    @Override
    public XMLTag marshal(String tagName, Object object, XMLTag parent) {
        if (object == null) return parent;

        for (Object item : (List<?>) object) {
            new RMarshaller().addFeatures(features).marshal(tagName, item, parent);
        }
        return parent;
    }

}
