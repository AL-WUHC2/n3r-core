package org.n3r.esql.parser;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.n3r.esql.map.EsqlRowMapper;

public class EsqlTableSqlMapper implements EsqlRowMapper {

    @Override
    public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
        String[] row = new String[3];
        row[0] = rs.getString(1);
        row[1] = rs.getString(2);
        row[2] = rs.getString(3);
        return row;
    }
}
