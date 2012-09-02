package org.n3r.esql.map;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.n3r.core.lang.RStr;
import org.n3r.esql.ex.EsqlExecuteException;

public class EsqlBaseBeanMapper {
    protected Class<?> mappedClass;
    protected Map<String, PropertyDescriptor> mappedFields;

    public EsqlBaseBeanMapper(Class<?> mappedClass) {
        initialize(mappedClass);
    }

    protected void initialize(Class<?> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<String, PropertyDescriptor>();
        PropertyDescriptor[] pds = getBeanInfo(mappedClass).getPropertyDescriptors();
        for (PropertyDescriptor pd : pds)
            if (pd.getWriteMethod() != null) {
                this.mappedFields.put(pd.getName().toLowerCase(), pd);
                String underscoredName = RStr.underscore(pd.getName());
                if (!pd.getName().toLowerCase().equals(underscoredName)) this.mappedFields.put(underscoredName, pd);
            }
    }

    protected BeanInfo getBeanInfo(Class<?> mappedClass) {
        try {
            return Introspector.getBeanInfo(mappedClass);
        }
        catch (IntrospectionException e) {
            throw new EsqlExecuteException(e);
        }
    }
}
