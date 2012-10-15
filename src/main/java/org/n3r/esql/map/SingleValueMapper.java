package org.n3r.esql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.n3r.esql.util.EsqlUtils.*;

public class SingleValueMapper implements EsqlRowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        return getResultSetValue(rs, 1);
    }

}
