package org.n3r.core.xml;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

import static java.beans.Introspector.*;

public abstract class FieldsTraverser {

    public void traverseFields(Class<?> clazz) {
        try {
            BeanInfo beanInfo = getBeanInfo(clazz, Object.class);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                processFields(pd);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public abstract void processFields(PropertyDescriptor pDescriptor) throws Exception;

}
