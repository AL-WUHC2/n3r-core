package org.n3r.core.xml.unmarshal;

import java.util.ArrayList;
import java.util.List;

import org.n3r.core.xml.UnmarshalAware;
import org.n3r.core.xml.annotation.RXBindTo;
import org.n3r.core.xmltool.XMLTag;

@RXBindTo(List.class)
public class ListUnmarshaller<T> implements UnmarshalAware<List<T>> {

    @Override
    public List<T> unmarshal(XMLTag xmlNode, Class<?> clazz) {
        String tagName = xmlNode.getCurrentTagName();

        List<T> result = new ArrayList<T>();
        XMLTag parent = xmlNode.gotoParent();
        for (XMLTag child : parent.getChilds()) {
            if (!tagName.equals(child.getCurrentTagName())) continue;

            result.add(new RUnmarshaller<T>().unmarshal(child, clazz));
        }
        return result;
    }
}
