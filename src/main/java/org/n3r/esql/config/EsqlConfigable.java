package org.n3r.esql.config;

import org.n3r.esql.EsqlTransaction;

public interface EsqlConfigable {
    EsqlTransaction getTransaction();

    String getSqlfromdb();
}
