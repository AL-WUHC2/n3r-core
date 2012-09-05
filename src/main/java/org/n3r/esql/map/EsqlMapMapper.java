package org.n3r.esql.map;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.n3r.esql.util.EsqlUtils.*;

public class EsqlMapMapper implements EsqlRowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, String> row = new HashMap<String, String>();
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); ++i) {
            String column = lookupColumnName(metaData, i);
            Object value = getResultSetValue(rs, i, String.class);
            row.put(column, toStr(value));
        }

        return row;
    }

    private String toStr(Object object) {
        if (object == null) return null;

        return object.toString().trim();
    }

}
