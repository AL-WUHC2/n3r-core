package org.n3r.core.xml.utils;

import org.n3r.core.xmltool.XMLTag;

import static org.n3r.core.xmltool.XMLDoc.*;

public class RXmlUtils {

    public static XMLTag buildCurrentTag(String tagName, XMLTag parent) {
        return parent == null ? newDocument(false).addRoot(tagName) : tagName == null ? parent : parent.addTag(tagName);
    }

}
