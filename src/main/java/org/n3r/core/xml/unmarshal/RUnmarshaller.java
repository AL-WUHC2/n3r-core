package org.n3r.core.xml.unmarshal;

import java.lang.reflect.Field;
import java.util.List;

import org.n3r.core.collection.RList;
import org.n3r.core.joor.Reflect;
import org.n3r.core.xml.UnmarshalAware;
import org.n3r.core.xmltool.XMLTag;

import static org.apache.commons.lang3.StringUtils.*;
import static org.n3r.core.joor.Reflect.*;
import static org.n3r.core.lang.RField.*;
import static org.n3r.core.lang.RType.*;
import static org.n3r.core.xml.utils.RJaxbClassesScanner.*;

public class RUnmarshaller implements UnmarshalAware {

    @Override
    public <T> T unmarshal(XMLTag xmlNode, Class<T> clazz) {
        UnmarshalAware unmarshaller = getUnmarshaller(clazz);
        if (unmarshaller != null) return unmarshaller.unmarshal(xmlNode, clazz);

        T object = on(clazz).create().get();
        Reflect reflect = on(object);
        for (XMLTag child : xmlNode.getChilds()) {
            String nodeName = uncapitalize(child.getCurrentTagName());
            Field field = getDeclaredField(clazz, nodeName);
            if (field != null) {
                reflect.set(field, new RUnmarshaller().unmarshal(child, field.getType()));
                continue;
            }

            field = getDeclaredField(clazz, nodeName + "s");
            if (field == null) continue;

            Class<?> fieldActualClazz = getActualTypeArgument(field.getGenericType());
            if (fieldActualClazz == Void.class) throw new RuntimeException("Unkown List Item Class for field "
                    + field.getName());

            reflect.set(field, RList.add(reflect.<List<Object>> get(field),
                    new RUnmarshaller().unmarshal(child, fieldActualClazz)));
        }

        return object;
    }
}
