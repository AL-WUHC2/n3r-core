package org.n3r.esql.map;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.n3r.core.joor.Reflect;
import org.n3r.core.lang.RStr;
import org.n3r.esql.ex.EsqlExecuteException;
import org.n3r.esql.util.EsqlUtils;

public class EsqlBeanRowMapper implements EsqlRowMapper {
    private Class<?> mappedClass;
    private Map<String, PropertyDescriptor> mappedFields;

    public EsqlBeanRowMapper(Class<?> mappedClass) {
        initialize(mappedClass);
    }

    protected void initialize(Class<?> mappedClass) {
        this.mappedClass = mappedClass;
        this.mappedFields = new HashMap<String, PropertyDescriptor>();
        PropertyDescriptor[] pds = getBeanInfo(mappedClass).getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() != null) {
                this.mappedFields.put(pd.getName().toLowerCase(), pd);
                String underscoredName = RStr.underscore(pd.getName());
                if (!pd.getName().toLowerCase().equals(underscoredName)) {
                    this.mappedFields.put(underscoredName, pd);
                }
            }
        }
    }

    private BeanInfo getBeanInfo(Class<?> mappedClass) {
        try {
            return Introspector.getBeanInfo(mappedClass);
        }
        catch (IntrospectionException e) {
            throw new EsqlExecuteException(e);
        }
    }

    public Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Object mappedObject = Reflect.on(this.mappedClass).create().get();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int index = 1; index <= columnCount; index++) {
            String column = EsqlUtils.lookupColumnName(rsmd, index);
            PropertyDescriptor pd = this.mappedFields.get(column.replaceAll(" ", "").toLowerCase());
            if (pd != null) {
                Object value = EsqlUtils.getResultSetValue(rs, index, pd.getPropertyType());
                Reflect.on(mappedObject).set(pd.getName(), value);
            }
        }

        return mappedObject;
    }

}
