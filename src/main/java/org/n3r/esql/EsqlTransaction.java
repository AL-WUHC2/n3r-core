package org.n3r.esql;

import java.io.Closeable;
import java.sql.Connection;

public interface EsqlTransaction extends Closeable{

    void start();

    void commit();
    
    void rollback();

    Connection getConnection();
}
