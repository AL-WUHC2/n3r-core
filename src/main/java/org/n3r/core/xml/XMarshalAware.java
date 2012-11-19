package org.n3r.core.xml;

import org.n3r.core.xmltool.XMLTag;

public interface XMarshalAware {

    XMLTag marshal(String tagName, Object object, XMLTag parent, boolean isCData);

}
