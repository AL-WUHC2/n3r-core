package org.n3r.esql.map;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.n3r.core.joor.Reflect;
import org.n3r.esql.util.EsqlUtils;

public class EsqlBeanRowMapper extends EsqlBaseBeanMapper implements EsqlRowMapper {
    public EsqlBeanRowMapper(Class<?> mappedClass) {
        super(mappedClass);
    }

    @Override
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
