package org.n3r.esql.trans;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.n3r.esql.EsqlTran;
import org.n3r.esql.ex.EsqlExecuteException;

public class EsqlJdbcTransaction implements EsqlTran {
    private Connection connection;

    public EsqlJdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void start() {
        try {
            if (connection == null) throw new EsqlExecuteException(
                    "EsqlJdbcTransaction could not start transaction. " +
                            " Cause: The DataSource returned a null connection.");

            if (connection.getAutoCommit()) connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new EsqlExecuteException(e);
        }
    }

    @Override
    public void commit() {
        if (connection == null) return;

        try {
            connection.commit();
        } catch (SQLException e) {
            throw new EsqlExecuteException(e);
        }
    }

    @Override
    public void rollback() {
        if (connection == null) return;

        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new EsqlExecuteException(e);
        }
    }

    @Override
    public Connection getConn() {
        return connection;
    }

    /**
     * Oracle JDBC会在close时自动commit(如果没有显式调用commit/rollback时).
     */
    @Override
    public void close() throws IOException {
        if (connection == null) return;

        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {
            // Ingore
        }
    }

}
