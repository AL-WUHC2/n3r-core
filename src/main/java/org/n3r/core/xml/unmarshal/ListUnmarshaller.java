package org.n3r.core.xml.unmarshal;

import java.util.ArrayList;
import java.util.List;

import org.n3r.core.xml.annotation.RXBindTo;
import org.n3r.core.xml.impl.BaseUnmarshaller;
import org.n3r.core.xmltool.XMLTag;

@RXBindTo(List.class)
public class ListUnmarshaller extends BaseUnmarshaller {

    @Override
    public Object unmarshal(XMLTag xmlNode, Class<?> clazz) {
        String tagName = xmlNode.getCurrentTagName();

        List<Object> result = new ArrayList<Object>();
        XMLTag parent = xmlNode.gotoParent();
        for (XMLTag child : parent.getChilds()) {
            if (!tagName.equals(child.getCurrentTagName())) continue;
            Class<?> actualClass = getActualClass(child, clazz);
            if (actualClass.equals(Void.class) && typeMapping == null) continue;

            result.add(new RUnmarshaller().setTypeMapping(typeMapping).unmarshal(child, actualClass));
        }

        return result;
    }

}
