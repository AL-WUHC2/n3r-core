package org.n3r.core.xml;

import org.n3r.core.xmltool.XMLTag;

public interface UnmarshalAware<T> {

    T unmarshal(XMLTag xmlNode, Class<?> clazz);

}
