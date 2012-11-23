package org.n3r.core.xml.marshal;

import org.n3r.core.xml.annotation.RXBindTo;
import org.n3r.core.xml.impl.BaseMarshaller;
import org.n3r.core.xmltool.XMLTag;

import static org.n3r.core.lang.RStr.*;

@RXBindTo( { String.class, Boolean.class, Byte.class, Character.class,
        Short.class, Integer.class, Long.class, Double.class, Float.class })
public class SimpleMarshaller extends BaseMarshaller {

    @Override
    public XMLTag marshal(String tagName, Object object, XMLTag parent) {
        parent = buildCurrentTag(tagName, parent);
        writeObjectClass(object.getClass(), parent);

        return parent.addText(toStr(object));
    }

}
