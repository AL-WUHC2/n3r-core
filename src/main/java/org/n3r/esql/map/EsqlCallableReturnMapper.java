package org.n3r.esql.map;

import java.sql.CallableStatement;
import java.sql.SQLException;

import org.n3r.esql.res.EsqlSub;

public interface EsqlCallableReturnMapper {
    Object mapResult(EsqlSub subSql, CallableStatement cs) throws SQLException;
}
