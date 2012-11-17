package org.n3r.core.xml.unmarshal;

import org.apache.commons.lang3.ClassUtils;
import org.n3r.core.joor.Reflect;
import org.n3r.core.xml.UnmarshalAware;
import org.n3r.core.xml.annotation.RXBindTo;
import org.n3r.core.xmltool.XMLTag;

@RXBindTo( { String.class, Boolean.class, Byte.class, Character.class,
        Short.class, Integer.class, Long.class, Double.class, Float.class })
public class SimpleUnmarshaller<T> implements UnmarshalAware<T> {

    @Override
    public T unmarshal(XMLTag xmlNode, Class<?> clazz) {
        return Reflect.on(ClassUtils.primitiveToWrapper(clazz)).call("valueOf", xmlNode.getText()).get();
    }

}
