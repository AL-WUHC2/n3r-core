package org.n3r.esql.map;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EsqlRowMapper {
    /** 
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call <code>next()</code> on
     * the ResultSet; it is only supposed to map values of the current row.
     * @param rs the ResultSet to map (pre-initialized for the current row)
     * @param rowNum the number of the current row
     * @return the result object for the current row
     * @throws SQLException if a SQLException is encountered getting
     * column values (that is, there's no need to catch SQLException)
     */
    Object mapRow(ResultSet rs, int rowNum) throws SQLException; 
}
