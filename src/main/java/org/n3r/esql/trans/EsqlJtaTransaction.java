package org.n3r.esql.trans;

import java.io.IOException;
import java.sql.Connection;

import org.n3r.esql.Esql;
import org.n3r.esql.EsqlTransaction;

public class EsqlJtaTransaction implements EsqlTransaction {

    public void close() throws IOException {
        Esql.execContext.get().setTransaction(null);
    }

    public void start() {

    }

    public void commit() {

    }

    public void rollback() {

    }

    public Connection getConnection() {
        return null;
    }

}
