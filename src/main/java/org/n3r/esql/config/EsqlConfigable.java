package org.n3r.esql.config;

import org.n3r.esql.EsqlTran;

public interface EsqlConfigable {
    EsqlTran getTran();

    String getSqlfromdb();
}
