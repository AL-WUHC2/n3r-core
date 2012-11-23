package org.n3r.core.xml;

import org.n3r.core.xmltool.XMLTag;

public interface XUnmarshalAware {

    Object unmarshal(XMLTag xmlNode, Class<?> clazz);

}
