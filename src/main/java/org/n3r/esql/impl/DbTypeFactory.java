package org.n3r.esql.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.n3r.esql.DbType;
import org.n3r.esql.ex.EsqlException;

public class DbTypeFactory {

    public static DbType parseDbType(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            String driverName = metaData.getDriverName();

            DbType dbType = new DbType();
            dbType.setDriverName(driverName);

            return dbType;
        }
        catch(SQLException ex) {
            throw new EsqlException(ex);
        }

    }

}
